### Názov aplikácie: Posts Service

---

### Popis:

Aplikácia Posts Service je webová služba navrhnutá na správu príspevkov.
Umožňuje užívateľom vytvárať, upravovať, prehliadať a mazať príspevky.
Aplikácia poskytuje aj užívateľské rozhranie pre jednoduchšie používanie.

---

### Požiadavky na spustenie:
 - Maven - pri vývoji bola použitá verzia 3.6.3
 - Java 17

---

### Inštalácia a spustenie:

1.Skompilujte aplikáciu pomocou Apache Maven v zložke projektu:
    mvn package

2.Spustite kontajner s MongoDB pomocou Docker Compose:
    docker-compose up -d

3.Spustite aplikáciu Posts Service:
    java -jar target/posts-service-0.0.1-SNAPSHOT.jar

Po spustení je aplikácia dostupná na porte 8080.
Užívateľské rozhranie aplikácie je testované v prehliadači
Chrome a je dostupné na adrese http://localhost:8080.
API dokumentácia je dostupná na adrese
http://localhost:8080/swagger-ui/index.html#/.

