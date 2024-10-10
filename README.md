# Currency Conversor

Este proyecto es un conversor de divisas que utiliza la API de [ExchangeRate-API](https://www.exchangerate-api.com/) para obtener las tasas de cambio entre diferentes monedas. El proyecto está estructurado bajo los principios de **Clean Architecture** y **SOLID**.

## Características

- Conocer las monedas disponibles para realizar conversiones.
- Obtener la tasa de conversión entre dos monedas.
- Realizar la conversión de un monto de una moneda a otra.
- Menú interactivo para seleccionar las diferentes opciones.

## Estructura del Proyecto

El proyecto está organizado de la siguiente manera:

- **com.oelnooc.currencyconversor.data.model**: Contiene las clases `Currency` y el enum `CurrencyType` para manejar las monedas.
- **com.oelnooc.currencyconversor.data.api**: Contiene la clase `Request` para realizar las solicitudes HTTP a la API de ExchangeRate.
- **com.oelnooc.currencyconversor.util.model**: Contiene la clase `Menu`, que se encarga de la interacción con el usuario.

## Requisitos

- Java 11
- GSON 2.11
- IntelliJ IDEA Community Edition

## Instalación

1. Clona este repositorio en tu máquina local:

    ```bash
    git clone https://github.com/tuusuario/currency-conversor.git
    ```

2. Instala las dependencias necesarias (asegúrate de que tienes GSON y Java 11 instalados).

3. Configura tu API Key de la ExchangeRate-API:
   
    - Crea un archivo llamado `config.properties` en la ruta `src/resources/` del proyecto.
    - Dentro del archivo, agrega lo siguiente:

      ```properties
      api.key=TUAPIKEY
      ```

    - Asegúrate de agregar `config.properties` al archivo `.gitignore` para que tu API Key no se suba al repositorio.

4. Ejecuta el proyecto en IntelliJ IDEA.

## Uso

### Menú de Opciones

Al ejecutar el programa, tendrás las siguientes opciones:

1. **Conocer las monedas disponibles**: Muestra la lista de monedas soportadas por la API.
   
2. **Conocer la tasa de conversión desde una moneda a otra**: Se pedirá ingresar la moneda base y la moneda de destino, y se devolverá la tasa de conversión y la hora de la consulta. Por ejemplo:
   
   ```text
   A las 19:04 del 10-10-2024, la tasa de CLP a USD es de 0.54.
   ```
3. **Realizar conversión de una moneda a otra**: 
   El programa solicita la moneda base, la moneda de destino y el monto a convertir. Luego, devuelve el monto convertido. Por ejemplo:

   ```text
   Ingrese la moneda base: USD
   Ingrese la moneda destino: CLP
   Ingrese el monto a convertir: 100
   El monto convertido es: 81500 CLP
   ```

4. **Salir**: Finaliza la ejecución del programa.

## Clases Principales

### Request

Clase que se encarga de realizar las solicitudes a la API y devolver los datos de las tasas de cambio.

```java
public class Request {
    private static final String CONFIG_FILE_PATH = "src/resources/config.properties";
    private static Request instance;
    private final Gson gson;
    private final String apiKey;
    // ...
    
     public Currency fetchExchangeRates(CurrencyType baseCurrency) throws IOException {
        // Método para obtener las tasas de cambio
    }
}
```

### Currency

Clase que modela la respuesta de la API, con información sobre las tasas de conversión y las fechas de actualización.

```java
public class Currency {
    private String result;
    private String documentation;
    private String terms_of_use;
    private String time_last_update_unix;
    private String time_last_update_utc;
    private String time_next_update_unix;
    private String time_next_update_utc;
    private String base_code;
    private Map<String, Double> conversion_rates;

    //...
    public Double getRate(CurrencyType currencyType) {
        if (conversion_rates != null) {
            return conversion_rates.get(currencyType.getCode());
        }
        return null;
    }
}
```

### CurrencyType (Enum)

Enum que contiene los códigos de las monedas soportadas por la API. Cada moneda tiene su nombre y código asociado.

```java
public enum CurrencyType {
    USD("United States Dollar", "USD"),
    CLP("Chilean Peso", "CLP");
    // ...

    public static CurrencyType fromCode(String code) {
        for (CurrencyType currency : CurrencyType.values()) {
            if (currency.code.equalsIgnoreCase(code)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Código de moneda inválido: " + code);
    }
  //...
}
```

### Menu

Clase que implementa el ciclo del menú con un `switch-case` para manejar las opciones.

```java
public class Menu {
    private final Scanner scanner;
    private final Request request;

    //...

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

    //...
}
```


