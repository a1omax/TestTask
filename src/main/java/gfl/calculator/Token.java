package gfl.calculator;

public class Token implements Comparable<Token> {

    // Name of token
    protected TokenName tokenName;
    // Content of token
    protected String content;
    // Start and end position for sorting after initial parsing
    private final int startPosition;
    private final int endPosition;

    Token(TokenName tokenName, String content, int startPosition, int endPosition){
        this.tokenName=tokenName;
        this.content=content;
        this.startPosition=startPosition;
        this.endPosition=endPosition;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public TokenName getTokenName() {
        return tokenName;
    }

    public String getContent() {
        return content;
    }

    public void setTokenName(TokenName tokenName) {
        this.tokenName = tokenName;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public int compareTo(Token token) {
        // Compare using start position after parsing
        return Integer.compare(this.startPosition, token.getStartPosition());

    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenName=" + tokenName +
                ", content='" + content + '\'' +
                '}';
    }
}
