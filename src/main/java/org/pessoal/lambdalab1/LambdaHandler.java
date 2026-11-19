package org.pessoal.lambdalab1;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Código da função Lambda do Lab 1 (versão Java 17).
 *
 * Handler no console: com.example.demo.LambdaHandler::handleRequest
 * Runtime: Java 17
 */
public class LambdaHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    // FORA do método handler: criado 1 vez no INIT (cold start) e reaproveitado nas invocações
    private static final Gson GSON = new Gson();

    // Lendo variável de ambiente configurada no console (também lida só no INIT)
    private static final String ENVIRONMENT = System.getenv().getOrDefault("APP_ENV", "dev");

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        // Lendo dados do evento
        String name = String.valueOf(event.getOrDefault("name", "Mundo"));
        String action = String.valueOf(event.getOrDefault("action", "greet"));

        // Informações do contexto (injetadas pelo runtime)
        int remainingTime = context.getRemainingTimeInMillis();
        String functionName = context.getFunctionName();
        String requestId = context.getAwsRequestId();

        System.out.println("[" + ENVIRONMENT + "] Invocando função: " + functionName);
        System.out.println("Request ID: " + requestId);
        System.out.println("Tempo restante: " + remainingTime + "ms");
        System.out.println("Evento recebido: " + GSON.toJson(event));

        String message = switch (action) {
            case "greet" -> "Olá, " + name + "! Ambiente: " + ENVIRONMENT;
            case "echo" -> "Echo: " + GSON.toJson(event);
            default -> "Ação desconhecida: " + action;
        };

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", message);
        body.put("requestId", requestId);
        body.put("remainingTime", remainingTime);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("statusCode", 200);
        response.put("headers", Map.of("Content-Type", "application/json"));
        response.put("body", GSON.toJson(body));

        return response;
    }
}