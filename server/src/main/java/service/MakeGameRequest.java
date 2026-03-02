package service;

public record MakeGameRequest(
        String token,
        String gameName
) {
}
