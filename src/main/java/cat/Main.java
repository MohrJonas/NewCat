package cat;

import cat.tokenization.Token;
import cat.tokenization.Tokenizer;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

import static cTools.KernelWrapper.*;

public class Main {

	private static CatConfig config;

	public static void main(String[] args) {
		final ImmutableList<Token> tokens = Tokenizer.tokenize(args);
		TokenValidator.validateTokens(tokens);
		config = new CatConfig(tokens);
		if (config.isShowHelp()) {
			showHelpMessage();
			exit(0);
		}
		config.getFiles().forEach(file -> {
			final int fd = file.equals("-") ? STDIN_FILENO : open(file, O_RDONLY);
			final byte[] byteBuffer = new byte[1];
			final ArrayList<Byte> currentLine = new ArrayList<>();
			final ArrayList<String> lines = new ArrayList<>();
			while (read(fd, byteBuffer, 1) != 0) {
				if (byteBuffer[0] == 10) {
					final String line = convertToString(currentLine);
					lines.add(line);
					currentLine.clear();
				} else
					currentLine.add(byteBuffer[0]);
			}
			final String line = convertToString(currentLine);
			lines.add(line);
			printLines(processFurther(lines));
			close(fd);
		});
		exit(0);
	}

	private static ArrayList<String> processFurther(ArrayList<String> lines) {
		return lines.stream().map(line -> {
			if (config.isSqueezeBlank()) {
				line = squeezeBlanks(line);
			}
			if (config.isNumberLines() && line != null) {
				line = prependLineNr(line);
			}
			return line;
		}).filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
	}

	private static String prependLineNr(String line) {
		config.setCurrentLine(config.getCurrentLine() + 1);
		return "     " + (config.getCurrentLine() - 1) + " " + line;
	}

	private static @Nullable String squeezeBlanks(String s) {
		if (!s.isBlank() && !s.isEmpty()) {
			config.setCurrentEmptyLine(0);
			return s;
		}
		if (config.getCurrentEmptyLine() == 0) {
			config.setCurrentEmptyLine(config.getCurrentEmptyLine() + 1);
			return s;
		} else {
			config.setCurrentEmptyLine(config.getCurrentEmptyLine() + 1);
			return null;
		}
	}

	private static String convertToString(ArrayList<Byte> bytes) {
		final byte[] byteArr = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			byteArr[i] = bytes.get(i);
		}
		return Charset.defaultCharset().decode(ByteBuffer.wrap(byteArr)).toString();
	}

	private static void printLines(ArrayList<String> strings) {
		strings.forEach(System.out::println);
	}

	private static void showHelpMessage() {
		System.out.println("Usage:");
		System.out.println("NewCat [--help] [-n] [-e] [files...]");
	}

}
