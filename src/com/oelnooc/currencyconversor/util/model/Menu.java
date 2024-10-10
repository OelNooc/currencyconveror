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

    /**
     * Constructor que inicializa el Scanner y la instancia Singleton de Request.
     *
     * @throws IOException Si hay un error al inicializar la instancia de Request.
     */
    public Menu() throws IOException {
        this.scanner = new Scanner(System.in);
        this.request = Request.getInstance();
    }

    /**
     * Método principal para ejecutar el menú.
     */
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

    /**
     * Muestra el menú de opciones al usuario.
     */
    private void displayMenu() {
        System.out.println("\n--- Menú de Conversor de Monedas ---");
        System.out.println("1. Conocer las monedas disponibles");
        System.out.println("2. Conocer la tasa de conversión desde una moneda a otra");
        System.out.println("3. Realizar conversión de una moneda a otra");
        System.out.println("4. Salir");
        System.out.print("Ingresa tu opción (1-4): ");
    }

    /**
     * Obtiene la opción seleccionada por el usuario.
     *
     * @return número de opción
     */
    private int getUserChoice() {
        int choice = -1;
        String input = scanner.nextLine().trim();
        try {
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            // Opción inválida, se manejará en el switch-case
        }
        return choice;
    }

    /**
     * Lista todas las monedas disponibles utilizando el enum CurrencyType.
     */
    private void listAvailableCurrencies() {
        System.out.println("\n--- Lista de Monedas Disponibles ---");
        for (CurrencyType currency : CurrencyType.values()) {
            System.out.println(currency);
        }
    }

    /**
     * Muestra la tasa de conversión desde una moneda a otra, incluyendo la fecha y hora de la consulta.
     */
    private void showConversionRate() {
        try {
            System.out.println("\n--- Consultar Tasa de Conversión ---");
            // Seleccionar moneda base (origen)
            CurrencyType baseCurrency = selectCurrency("base");

            // Seleccionar moneda destino
            CurrencyType destinationCurrency = selectCurrency("destino");

            // Obtener las tasas de cambio
            Currency currencyData = request.fetchExchangeRates(baseCurrency);

            // Obtener la tasa de conversión
            Double rate = currencyData.getRate(destinationCurrency);
            if (rate == null) {
                System.out.println("No se encontró la tasa de conversión para " + destinationCurrency.getCode());
                return;
            }

            // Obtener la fecha y hora de la última actualización
            String updateTime = formatUpdateTime(currencyData.getTime_last_update_unix());

            // Mostrar la tasa de conversión
            System.out.printf("A las %s, la tasa de %s a %s es de %.4f%n",
                    updateTime,
                    baseCurrency.getCode(),
                    destinationCurrency.getCode(),
                    rate);
        } catch (IOException e) {
            System.out.println("Error al obtener las tasas de cambio: " + e.getMessage());
        }
    }

    /**
     * Realiza la conversión de una cantidad de una moneda a otra.
     */
    private void performCurrencyConversion() {
        try {
            System.out.println("\n--- Realizar Conversión de Moneda ---");
            // Seleccionar moneda base (origen)
            CurrencyType baseCurrency = selectCurrency("base");

            // Seleccionar moneda destino
            CurrencyType destinationCurrency = selectCurrency("destino");

            // Solicitar cantidad a convertir
            double amount = getAmountToConvert();

            // Obtener las tasas de cambio
            Currency currencyData = request.fetchExchangeRates(baseCurrency);

            // Obtener la tasa de conversión
            Double rate = currencyData.getRate(destinationCurrency);
            if (rate == null) {
                System.out.println("No se encontró la tasa de conversión para " + destinationCurrency.getCode());
                return;
            }

            // Calcular el monto convertido
            double convertedAmount = amount * rate;

            // Obtener la fecha y hora de la última actualización
            String updateTime = formatUpdateTime(currencyData.getTime_last_update_unix());

            // Mostrar el resultado de la conversión
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

    /**
     * Solicita al usuario que seleccione una moneda.
     *
     * @param type Tipo de selección ("base" o "destino")
     * @return CurrencyType seleccionado
     */
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

    /**
     * Solicita al usuario que ingrese una cantidad válida para convertir.
     *
     * @return cantidad a convertir
     */
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

    /**
     * Formatea la fecha y hora de actualización a partir del timestamp Unix.
     *
     * @param unixTimeString Tiempo en formato Unix.
     * @return Fecha y hora formateadas en zona horaria local.
     */
    private String formatUpdateTime(String unixTimeString) {
        try {
            long unixTime = Long.parseLong(unixTimeString);
            Instant instant = Instant.ofEpochSecond(unixTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm 'del' dd-MM-yyyy")
                    .withZone(ZoneId.systemDefault());
            return formatter.format(instant);
        } catch (Exception e) {
            return "Fecha y hora desconocidas"; // Retornar un mensaje por defecto si hay error
        }
    }
}