package org.example.project.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Parser {
    private List<String> vasilyevoTimes;
    private List<String> kazanTimes;

    public Parser() {
        try {
            Document document = Jsoup.connect("https://zpatp.ru/timetable").get();
            Element articleBody = document.selectFirst("div[itemprop=articleBody]");

            if (articleBody != null) {
                Elements h5Elements = articleBody.select("h5:contains(№ 110 \"ВАСИЛЬЕВО - ОСИНОВО - КАЗАНЬ\")");
                Element h5 = h5Elements.first();
                this.vasilyevoTimes = new ArrayList<>();
                this.kazanTimes = new ArrayList<>();

                Element sibling = h5.nextElementSibling();
                while (sibling != null && sibling.tagName().equals("ul")) {
                    Elements liElements = sibling.select("li");

                    for (Element li : liElements) {
                        Elements pElements = li.select("p");
                        for (Element p : pElements) {
                            String text = p.text();
                            if (text.startsWith("Пункт отправления: Васильево")) {
                                for (Element pElem : li.select("p:contains(Время:)")) {
                                    String timesText = pElem.text().replace("Время: ", "").trim();
                                    String[] timeArray = timesText.split(", ");
                                    this.vasilyevoTimes.addAll(Arrays.asList(timeArray));
                                }
                            } else if (text.startsWith("Пункт отправления: Казань")) {
                                for (Element pElem : li.select("p:contains(Время:)")) {
                                    String timesText = pElem.text().replace("Время: ", "").trim();
                                    String[] timeArray = timesText.split(", ");
                                    this.kazanTimes.addAll(Arrays.asList(timeArray));
                                }
                            }
                        }
                    }
                    sibling = Objects.requireNonNull(sibling.nextElementSibling()).nextElementSibling();
                }
            } else {
                System.out.println("Element not found");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
