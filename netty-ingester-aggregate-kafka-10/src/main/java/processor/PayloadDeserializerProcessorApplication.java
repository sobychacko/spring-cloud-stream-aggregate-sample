package processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.serializer.Deserializer;
import org.springframework.util.StreamUtils;

/**
 * @author Soby Chacko
 */
@SpringBootApplication
@Import({org.springframework.cloud.stream.app.payload.deserializer.processor.PayloadDeserializerProcessorConfiguration.class,
		PayloadDeserializerProcessorApplication.DeserializerConfig.class})
public class PayloadDeserializerProcessorApplication {

	@Configuration
	static class DeserializerConfig {
		@Bean
		@ConditionalOnProperty(name="payload.deserializer.text")
		public Deserializer<Object> echoDeserializer() {
			return new EchoDeserializer();
		}

		@Bean
		@ConditionalOnProperty(name="payload.deserializer.binary")
		public Deserializer<Object> binaryEchoDeserializer() {
			return new BinaryDemoDataDeSerializer();
		}
	}

	static class EchoDeserializer implements Deserializer<Object> {

		@Override
		public String deserialize(InputStream inputStream) throws IOException {
			byte[] bytes = StreamUtils.copyToByteArray(inputStream);
			return new String(bytes);
		}
	}

	/**
	 * Deserializer for a contrived binary protocol:
	 *
	 * Structure of the frame
	 *
	 * length: 4 bytes
	 * text-field-count: 4 bytes
	 * text-filed-1-length: 4 bytes
	 * text field : text-filed-1-length bytes
	 * ....
	 * ....
	 * text-filed-n-length: 4 bytes
	 * text field : text-filed-n-length bytes
	 * numeric-data: 4 bytes
	 */
	static class BinaryDemoDataDeSerializer implements Deserializer<Object> {
		@Override
		public String deserialize(InputStream inputStream) throws IOException {
			byte[] bytes = StreamUtils.copyToByteArray(inputStream);
			int index = 0;
			long length = readUnsignedInt(bytes, index);
			index += 4;
			long stringCount = readUnsignedInt(bytes, index);
			index += 4;
			StringBuilder sb = new StringBuilder("[Text Content: ");
			for (int i = 0; i < stringCount; i++) {
				long textLength = readUnsignedInt(bytes, index);
				int endIndex = (index + 4 + (int)textLength);
				byte[] text = Arrays.copyOfRange(bytes, index + 4, endIndex);
				sb.append(new String(text)).append(", ");
				index = endIndex;
			}
			sb.append("]");
			long numericData = readUnsignedInt(bytes, index);
			return String.format("length: %d, {Numeric Elements: %d}, {Text Data: %s}",
					length, numericData, sb.toString());
		}

		private long readUnsignedInt(byte[] buffer, int pos) {
			return ((buffer[pos] & 0xffL) << 24) |
					((buffer[pos + 1] & 0xffL) << 16) |
					((buffer[pos + 2] & 0xffL) << 8) |
					(buffer[pos + 3] & 0xffL);
		}
	}
}
