package requests;

public record MakeGameRequest(
        String token,
        String gameName
) {
}
