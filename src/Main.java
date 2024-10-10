import com.oelnooc.currencyconversor.data.api.Request;
import com.oelnooc.currencyconversor.data.model.Currency;
import com.oelnooc.currencyconversor.data.model.CurrencyType;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try {
            Request request = Request.getInstance();
            Scanner scanner = new Scanner(System.in);

            // Mostrar todas las monedas soportadas
            System.out.println("Monedas soportadas:");
            for (CurrencyType type : CurrencyType.values()) {
                System.out.println(type);
            }

            // Solicitar al usuario la moneda base (y de origen)
            CurrencyType baseCurrency = null;
            while (baseCurrency == null) {
                System.out.print("Ingresa el código de la moneda base (ej. USD): ");
                String baseCode = scanner.nextLine().trim().toUpperCase();
                try {
                    baseCurrency = CurrencyType.fromCode(baseCode);
                } catch (IllegalArgumentException e) {
                    System.out.println("Código de moneda inválido. Por favor, intenta nuevamente.");
                }
            }

            // Obtener las tasas de cambio con la moneda base seleccionada
            Currency currency = request.fetchExchangeRates(baseCurrency);

            // Solicitar al usuario la moneda de destino
            CurrencyType toCurrency = null;
            while (toCurrency == null) {
                System.out.print("Ingresa el código de la moneda de destino (ej. EUR): ");
                String toCode = scanner.nextLine().trim().toUpperCase();
                try {
                    toCurrency = CurrencyType.fromCode(toCode);
                    if (toCurrency == baseCurrency) {
                        System.out.println("La moneda de destino no puede ser la misma que la moneda base. Por favor, elige una diferente.");
                        toCurrency = null;
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Código de moneda inválido. Por favor, intenta nuevamente.");
                }
            }

            // Solicitar al usuario la cantidad a convertir
            double amount = 0;
            boolean validAmount = false;
            while (!validAmount) {
                System.out.print("Ingresa la cantidad a convertir: ");
                String amountStr = scanner.nextLine().trim();
                try {
                    amount = Double.parseDouble(amountStr);
                    if (amount < 0) {
                        System.out.println("La cantidad no puede ser negativa. Por favor, intenta nuevamente.");
                    } else {
                        validAmount = true;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Cantidad inválida. Por favor, ingresa un número válido.");
                }
            }

            // Realizar la conversión
            double convertedAmount = convertCurrency(currency, toCurrency, amount);
            System.out.printf("%.2f %s = %.2f %s%n",
                    amount, baseCurrency.getCode(),
                    convertedAmount, toCurrency.getCode());

        } catch (Exception e) {
            System.err.println("Error al obtener las tasas de cambio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Convierte una cantidad de una moneda base a otra utilizando las tasas de cambio.
     *
     * @param currency     Objeto Currency con las tasas de cambio.
     * @param toCurrency   Moneda de destino.
     * @param amount       Cantidad a convertir.
     * @return Cantidad convertida.
     */
    public static double convertCurrency(Currency currency, CurrencyType toCurrency, double amount) {
        // Obtener la tasa de cambio de la moneda base a la moneda de destino
        Double toRate = currency.getRate(toCurrency);
        if (toRate == null) {
            throw new IllegalArgumentException("Tasa de cambio no disponible para " + toCurrency.getCode());
        }

        // Realizar la conversión
        return amount * toRate;
    }
}