package cat;

import cat.tokenization.TOKENS;
import cat.tokenization.Token;
import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
public class CatConfig {

	private final ImmutableList<String> files;
	private final ImmutableList<Token> tokens;
	private final boolean squeezeBlank;
	private final boolean numberLines;
	private final boolean showHelp;
	private int currentLine = 1;
	private int currentEmptyLine = 0;

	@Tolerate
	public CatConfig(ImmutableList<Token> tokens) {
		this.tokens = tokens;
		squeezeBlank = checkSqueeze(tokens);
		numberLines = checkNumber(tokens);
		showHelp = checkHelp(tokens);
		files = parseFiles(tokens);
	}

	private ImmutableList<String> parseFiles(ImmutableList<Token> tokens) {
		return tokens.stream()
				.filter(token -> token.getTokens() == TOKENS.FILE || token.getTokens() == TOKENS.STDIN)
				.map(Token::getCmd)
				.collect(ImmutableList.toImmutableList());
	}

	private boolean checkSqueeze(ImmutableList<Token> tokens) {
		return tokens.stream().map(Token::getTokens).anyMatch(token -> token == TOKENS.S);
	}

	private boolean checkNumber(ImmutableList<Token> tokens) {
		return tokens.stream().map(Token::getTokens).anyMatch(token -> token == TOKENS.N);
	}

	private boolean checkHelp(ImmutableList<Token> tokens) {
		return tokens.stream().map(Token::getTokens).anyMatch(token -> token == TOKENS.HELP);
	}

}
