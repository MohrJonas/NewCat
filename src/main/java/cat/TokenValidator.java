package cat;

import cat.tokenization.TOKENS;
import cat.tokenization.Token;
import com.google.common.collect.ImmutableList;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TokenValidator {

	public void validateTokens(ImmutableList<Token> tokens) {
		if (countStdins(tokens) > 1) Util.errorAndExit("Only 1 stdin is allowed, %d were provided", countStdins(tokens));
	}

	private int countStdins(ImmutableList<Token> tokens) {
		return (int) tokens.stream().filter(token -> token.getTokens() == TOKENS.STDIN).count();
	}

}
