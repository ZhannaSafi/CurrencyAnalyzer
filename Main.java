import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите дату: ");
        String userInput = scanner.nextLine();
        scanner.close();

        try {
            if (userInput.length() != 10) {
                throw new ParseException("Недопустимая длина даты", 0);
            }
            SimpleDateFormat[] possibleFormats = {
                    new SimpleDateFormat("dd.MM.yyyy"),
                    new SimpleDateFormat("dd-MM-yyyy")
            };
            Date date = null;

            for (SimpleDateFormat format: possibleFormats) {
                try {
                    date = format.parse(userInput);
                    break;
                } catch (ParseException ignored) {}
            }

            if (date == null) {
                throw new ParseException("Недопустимый формат даты", 0);
            }

            SimpleDateFormat desiredFormat = new SimpleDateFormat("dd/MM/yyyy");
            String userFormattedDate = desiredFormat.format(date);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate userFormattedDate1 = LocalDate.parse(userFormattedDate, formatter);

            LocalDate prevDate = userFormattedDate1.minusDays(1);
            LocalDate prevDate1 = prevDate.minusDays(1);
            LocalDate nextDate = userFormattedDate1.plusDays(1);

            String prevFormattedDate = prevDate.format(formatter);
            String prevFormattedDate1 = prevDate1.format(formatter);
            String nextFormattedDate = nextDate.format(formatter);

            try {

                double maxRate = Double.MIN_VALUE;
                double minRate = Double.MAX_VALUE;

                String pagePrevDate = Downloader.download("https://www.cbr.ru/scripts/XML_dynamic.asp?date_req1="
                        + prevFormattedDate1 + "&date_req2=" + prevFormattedDate + "&VAL_NM_RQ=R01235");
                int startIndex1 = pagePrevDate.lastIndexOf("<Value>");
                int endIndex1 = pagePrevDate.lastIndexOf("</Value>");
                String prevCourse = pagePrevDate.substring(startIndex1 + 7, endIndex1);
                System.out.println("Курс доллара на " + prevFormattedDate + " составляет: " + prevCourse + " руб.");
                double course1 = Double.parseDouble(prevCourse.replace(',', '.'));
                if (course1 > maxRate) {
                    maxRate = course1;
                }
                if (course1 < minRate) {
                    minRate = course1;
                }

                String pageUserDate = Downloader.download("https://www.cbr.ru/scripts/XML_dynamic.asp?date_req1="
                        + prevFormattedDate + "&date_req2=" + userFormattedDate + "&VAL_NM_RQ=R01235");
                int startIndex2 = pageUserDate.lastIndexOf("<Value>");
                int endIndex2 = pageUserDate.lastIndexOf("</Value>");
                String userInputCourse = pageUserDate.substring(startIndex2 + 7, endIndex2);
                System.out.println("Курс доллара на " + userFormattedDate + " составляет: " + userInputCourse + " руб.");
                double course2 = Double.parseDouble(userInputCourse.replace(',', '.'));
                if (course2 > maxRate) {
                    maxRate = course2;
                }
                if (course2 < minRate) {
                    minRate = course2;
                }

                String pageNextDate = Downloader.download("https://www.cbr.ru/scripts/XML_dynamic.asp?date_req1="
                        + userFormattedDate + "&date_req2=" + nextFormattedDate + "&VAL_NM_RQ=R01235");
                int startIndex3 = pageNextDate.lastIndexOf("<Value>");
                int endIndex3 = pageNextDate.lastIndexOf("</Value>");
                String nextCourse = pageNextDate.substring(startIndex3 + 7, endIndex3);
                System.out.println("Курс доллара на " + nextFormattedDate + " составляет: " + nextCourse + " руб.");
                double course3 = Double.parseDouble(nextCourse.replace(',', '.'));
                if (course3 > maxRate) {
                    maxRate = course3;
                }
                if (course3 < minRate) {
                    minRate = course3;
                }

                System.out.println();
                System.out.println("Максимальный курс доллара составил: " + maxRate);
                System.out.println("Минимальный курс доллара составил: " + minRate);

            } catch (Exception e) {
                System.out.println("В выходные дни курс доллара не предоставляется.");
            }

        } catch (ParseException | IllegalArgumentException e) {
            System.out.println("Недопустимый формат даты. Введите заново");
        } finally {
            scanner.close();
        }
    }
}
