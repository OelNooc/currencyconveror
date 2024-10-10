package com.oelnooc.currencyconversor.util.model;

import com.oelnooc.currencyconversor.data.api.Request;
import com.oelnooc.currencyconversor.data.model.Currency;
import com.oelnooc.currencyconversor.data.model.CurrencyType;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Menu {
    private final Scanner scanner;
    private final Request request;

    public Menu() throws IOException {
        this.scanner = new Scanner(System.in);
        this.request = Request.getInstance();
    }

    public void run() {
        while (true) {
            displayMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    listAvailableCurrencies();
                    break;
                case 2:
                    showConversionRate();
                    break;
                case 3:
                    performCurrencyConversion();
                    break;
                case 4:
                    System.out.println("Saliendo de la aplicación. ¡Hasta luego!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opción inválida. Por favor, elige una opción entre 1 y 4.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n--- Menú de Conversor de Monedas ---");
        System.out.println("1. Conocer las monedas disponibles");
        System.out.println("2. Conocer la tasa de conversión desde una moneda a otra");
        System.out.println("3. Realizar conversión de una moneda a otra");
        System.out.println("4. Salir");
        System.out.print("Ingresa tu opción (1-4): ");
    }

    private int getUserChoice() {
        int choice = -1;
        String input = scanner.nextLine().trim();
        try {
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
        }
        return choice;
    }

    private void listAvailableCurrencies() {
        System.out.println("\n--- Lista de Monedas Disponibles ---");
        for (CurrencyType currency : CurrencyType.values()) {
            System.out.println(currency);
        }
    }

    private void showConversionRate() {
        try {
            System.out.println("\n--- Consultar Tasa de Conversión ---");
            CurrencyType baseCurrency = selectCurrency("base");

            CurrencyType destinationCurrency = selectCurrency("destino");

            Currency currencyData = request.fetchExchangeRates(baseCurrency);

            Double rate = currencyData.getRate(destinationCurrency);
            if (rate == null) {
                System.out.println("No se encontró la tasa de conversión para " + destinationCurrency.getCode());
                return;
            }

            String updateTime = formatUpdateTime(currencyData.getTime_last_update_unix());

            System.out.printf("A las %s, la tasa de %s a %s es de %.4f%n",
                    updateTime,
                    baseCurrency.getCode(),
                    destinationCurrency.getCode(),
                    rate);
        } catch (IOException e) {
            System.out.println("Error al obtener las tasas de cambio: " + e.getMessage());
        }
    }

    private void performCurrencyConversion() {
        try {
            System.out.println("\n--- Realizar Conversión de Moneda ---");
            CurrencyType baseCurrency = selectCurrency("base");

            CurrencyType destinationCurrency = selectCurrency("destino");

            double amount = getAmountToConvert();

            Currency currencyData = request.fetchExchangeRates(baseCurrency);

            Double rate = currencyData.getRate(destinationCurrency);
            if (rate == null) {
                System.out.println("No se encontró la tasa de conversión para " + destinationCurrency.getCode());
                return;
            }

            double convertedAmount = amount * rate;

            String updateTime = formatUpdateTime(currencyData.getTime_last_update_unix());

            System.out.printf("A las %s, %.2f %s = %.2f %s%n",
                    updateTime,
                    amount,
                    baseCurrency.getCode(),
                    convertedAmount,
                    destinationCurrency.getCode());
        } catch (IOException e) {
            System.out.println("Error al obtener las tasas de cambio: " + e.getMessage());
        }
    }

    private CurrencyType selectCurrency(String type) {
        CurrencyType currency = null;
        while (currency == null) {
            System.out.printf("Ingresa el código de la moneda %s (ej. USD): ", type);
            String code = scanner.nextLine().trim().toUpperCase();
            try {
                currency = CurrencyType.fromCode(code);
            } catch (IllegalArgumentException e) {
                System.out.println("Código de moneda inválido. Por favor, intenta nuevamente.");
            }
        }
        return currency;
    }

    private double getAmountToConvert() {
        double amount = 0;
        boolean valid = false;
        while (!valid) {
            System.out.print("Ingresa la cantidad a convertir: ");
            String input = scanner.nextLine().trim();
            try {
                amount = Double.parseDouble(input);
                if (amount < 0) {
                    System.out.println("La cantidad no puede ser negativa. Por favor, intenta nuevamente.");
                } else {
                    valid = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Cantidad inválida. Por favor, ingresa un número válido.");
            }
        }
        return amount;
    }

    private String formatUpdateTime(String unixTimeString) {
        try {
            long unixTime = Long.parseLong(unixTimeString);
            Instant instant = Instant.ofEpochSecond(unixTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm 'del' dd-MM-yyyy")
                    .withZone(ZoneId.systemDefault());
            return formatter.format(instant);
        } catch (Exception e) {
            return "Fecha y hora desconocidas";
        }
    }
}