package cat.tokenization;

import cat.Util;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;

@UtilityClass
public class Tokenizer {

	private final ImmutableMap<TOKENS, String> tokenMap = ImmutableMap.of(
			TOKENS.S, "^-s$",
			TOKENS.N, "^-n$",
			TOKENS.HELP, "^--help$",
			TOKENS.STDIN, "^-$",
			TOKENS.FILE, "^[\\w|.]\\S*$"
	);

	public ImmutableList<Token> tokenize(String[] args) {
		if (args.length == 0) return ImmutableList.of(new Token("-", TOKENS.STDIN));
		return Arrays.stream(args).map(s -> {
			final @Nullable TOKENS token = findToken(s);
			if (token == null) Util.errorAndExit("Unparsable token %s", s);
			return new Token(s, token);
		}).collect(ImmutableList.toImmutableList());
	}

	private @Nullable TOKENS findToken(String s) {
		return tokenMap
				.entrySet()
				.stream()
				.filter(entry -> s.matches(entry.getValue()))
				.map(Map.Entry::getKey)
				.findFirst()
				.orElse(null);
	}
}
