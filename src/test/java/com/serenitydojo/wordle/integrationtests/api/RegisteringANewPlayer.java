package com.serenitydojo.wordle.integrationtests.api;

import com.github.javafaker.Faker;
import com.serenitydojo.wordle.microservices.authentication.Player;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SerenityJUnit5Extension.class)
@DisplayName("Registering a new user")
@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                classes = com.serenitydojo.wordle.microservices.WordleApplication.class)
public class RegisteringANewPlayer {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    String id;

    @Steps
    GameFacade gameFacade;

    Faker fake = Faker.instance();

    @Test
    @DisplayName("Players need to register before they can login and play")
    @Order(1)
    void registeringAsANewPlayer() {
        String name = fake.name().name();
        String email = fake.bothify("????##@gmail.com");
        String password = fake.bothify("????####");

        Player player = new Player(email, password, name);
        Long id = SerenityRest
                .with()
                .body(player)
                .contentType(ContentType.JSON)
                .post("/api/players/register")
                .getBody().as(Long.class);

        assertThat(id).isNotZero();
    }

    @Test
    @DisplayName("Password must not be empty")
    @Order(1)
    void registeringAsANewPlayerWithAnExistingEmail() {
        String name = fake.name().name();
        String email = fake.bothify("????##@gmail.com");

        Player player = new Player(email, "", name);
        SerenityRest
                .with()
                .body(player)
                .contentType(ContentType.JSON)
                .post("/api/players/register")
                        .then().statusCode(409);
    }

    @Test
    @DisplayName("Email must be unique")
    @Order(1)
    void registeringAsANewPlayerWithAMissingPassowrd() {
        String name = fake.name().name();
        String email = fake.bothify("????##@gmail.com");
        String password = "";

        Player player = new Player(email, password, name);
        SerenityRest
                .with()
                .body(player)
                .contentType(ContentType.JSON)
                .post("/api/players/register")
                .then().statusCode(409);
    }
}
