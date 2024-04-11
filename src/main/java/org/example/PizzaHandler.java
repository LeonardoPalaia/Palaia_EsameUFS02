package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class PizzaHandler implements HttpHandler {
    private List<Pizza> pizzas;

    public PizzaHandler() {
        pizzas = new ArrayList<>();
        //Inizializzo l'array creando alcune pizze
        pizzas.add(new Pizza("Margherita", Arrays.asList("pomodoro", "mozzarella"), 7));
        pizzas.add(new Pizza("Margherita senza formaggio", Collections.singletonList("pomodoro"),5));
        pizzas.add(new Pizza("Marinara", Arrays.asList("pomodoro", "aglio", "origano"), 8));
        pizzas.add(new Pizza("Capricciosa", Arrays.asList("pomodoro", "mozzarella", "funghi", "prosciutto", "carciofi", "olive"), 8.50));
        pizzas.add(new Pizza("Quattro formaggi", Arrays.asList("pomodoro", "mozzarella", "gorgonzola", "fontina", "parmigiano"), 8));
        pizzas.add(new Pizza("Diavola", Arrays.asList("pomodoro", "mozzarella", "salame piccante", "olio al peperoncino"), 7.50));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        System.out.println(uri);

        String command = exchange.getRequestURI().getQuery();
        if (command != null) {
            String[] params = command.split("=");
            if (params.length == 2 && params[0].equals("command")) {
                String s = params[1];

                System.out.println(s);

                String response;
                try {
                    switch (s) {
                        case "with_tomato":
                            response = getPizzasWithIngredient("pomodoro");
                            break;
                        case "with_cheese":
                            response = getPizzasWithIngredient("mozzarella");
                            break;
                        case "sorted_by_price":
                            response = getSortedPizzaListByPrice();
                            break;
                        default:
                            response = "Comando non valido\n";
                            break;
                    }
                } catch (Exception e) {
                    response = "Error: " + e.getMessage();
                    e.printStackTrace();
                }
                extracted(exchange, 200, response);
            }
            else {
                String response = "Il formato del comando non è valido";
                extracted(exchange, 400, response);
            }
        }
        else {
            String response = "Manca il parametro 'command'";
            extracted(exchange, 400, response);
        }
    }

    private String getPizzasWithIngredient(String ingredient) {
        List<Pizza> pizzasWithIngredient = pizzas.stream().filter(pizza -> pizza.getIngredients().contains(ingredient))
                .collect(Collectors.toList());

        StringBuilder response = new StringBuilder();
        for (Pizza pizza : pizzasWithIngredient) {
            response.append(pizza.getName()).append("\n");
        }
        return response.toString();
    }

    private String getSortedPizzaListByPrice() {
        List<Pizza> sortedPizzas = pizzas.stream().sorted(Comparator.comparing(Pizza::getPrice)).collect(Collectors.toList());

        StringBuilder response = new StringBuilder();
        for (Pizza pizza : sortedPizzas) {
            response.append(String.format("%-20s - €%.1f\n", pizza.getName(), pizza.getPrice()));
        }
        return response.toString();
    }

    private static void extracted(HttpExchange exchange, int rCode, String response) {
        try {
            exchange.sendResponseHeaders(rCode, response.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}