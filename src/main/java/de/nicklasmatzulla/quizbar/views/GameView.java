/*
 * Copyright 2023 Nicklas Matzulla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.nicklasmatzulla.quizbar.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoIcon;
import de.nicklasmatzulla.quizbar.config.Question;
import de.nicklasmatzulla.quizbar.config.QuestionConfiguration;
import de.nicklasmatzulla.quizbar.jpa.entity.StatisticsEntity;
import de.nicklasmatzulla.quizbar.jpa.entity.UserEntity;
import de.nicklasmatzulla.quizbar.jpa.repository.StatisticsRepository;
import de.nicklasmatzulla.quizbar.jpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@PageTitle("Spielen")
@Route(value = "game")
public class GameView extends VerticalLayout {

    private final UserRepository userRepository;
    private final StatisticsRepository statisticsRepository;
    private short groupSize = 0;
    private final HashMap<Short, ArrayList<StatisticsEntity>> statisticsEntities = new HashMap<>();
    private final UserEntity[] users = new UserEntity[4];
    private int buttonsAvailable = 25;
    private short currentUser = 1;
    private final H3 currentPlayerHeading = new H3();

    @Autowired
    public GameView(UserRepository userRepository, StatisticsRepository statisticsRepository) {
        this.userRepository = userRepository;
        this.statisticsRepository = statisticsRepository;
        showGroupSizeDialog();
    }

    private void nextPlayer() {
        currentUser = (short) ((currentUser % groupSize) + 1);
        UserEntity currentPlayer = getCurrentPlayer();
        currentPlayerHeading.setText(currentPlayer.getFullName() + " (" + currentPlayer.getPoints() + " Punkte)");
        if (buttonsAvailable == 0) {
            for (short userId = 0; userId < groupSize; userId++) {
                UserEntity user = users[userId];
                if (user != null && !userRepository.existsByFirstNameAndLastName(user.getFirstName(), user.getLastName())) {
                    short finalUserID = userId;
                    CompletableFuture.supplyAsync(() -> userRepository.save(user)).thenAcceptAsync(createdUser -> {
                        ArrayList<StatisticsEntity> statisticsEntities = this.statisticsEntities.getOrDefault(finalUserID, new ArrayList<>());
                        statisticsEntities.forEach(entity -> {
                            entity.setUserId(createdUser.getId());
                            this.statisticsRepository.save(entity);
                        });
                    });
                }
            }
            showRoundResultDialog();
        }
    }

    private UserEntity getCurrentPlayer() {
        return users[currentUser - 1];
    }

    private void showGame() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout currentPlayerLayout = new VerticalLayout();
        H2 title = new H2("Aktueller Spieler");
        currentPlayerHeading.setText(users[0].getFullName() + " (" + users[0].getPoints() + " Punkte)");
        currentPlayerLayout.add(title, currentPlayerHeading);
        currentPlayerLayout.setAlignItems(Alignment.CENTER);
        currentPlayerLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        add(currentPlayerLayout);

        final HorizontalLayout gameLayout = new HorizontalLayout();
        gameLayout.add(createCategoryLayout(Question.Category.GENERAL, "Generell"));
        gameLayout.add(createCategoryLayout(Question.Category.LITERATURE, "Literatur"));
        gameLayout.add(createCategoryLayout(Question.Category.HEALTH, "Gesundheit"));
        gameLayout.add(createCategoryLayout(Question.Category.ECONOMICS, "Wirtschaft"));
        gameLayout.add(createCategoryLayout(Question.Category.SPORTS, "Sport"));
        add(gameLayout);
    }

    private VerticalLayout createCategoryLayout(Question.Category category, String categoryName) {
        VerticalLayout categoryLayout = new VerticalLayout();
        Button categoryDescription = new Button(categoryName);
        categoryDescription.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        categoryDescription.setEnabled(false);
        categoryDescription.getStyle().set("background-color", "white");
        categoryDescription.getStyle().set("color", "black");
        categoryLayout.add(categoryDescription);

        for (int i = 200; i <= 1000; i += 200) {
            int finalI = i;
            Button categoryButton = new Button(String.valueOf(i), event -> processButton(category, finalI));
            categoryButton.setWidthFull();
            categoryButton.setDisableOnClick(true);
            categoryLayout.add(categoryButton);
        }

        categoryLayout.setAlignItems(Alignment.CENTER);
        categoryLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        return categoryLayout;
    }

    private void processButton(final Question.Category category, final int points) {
        QuestionConfiguration configuration = QuestionConfiguration.getInstance();
        Question question = configuration.findNextQuestion(category, points);
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.setHeaderTitle(question.getQuestion());
        HorizontalLayout buttonLayout = new HorizontalLayout();
        short correctAnswer = question.getCorrectAnswerId();
        for (short i = 1; i <= 4; i++) {
            short clickedAnswer = i;
            Button answerButton = new Button(question.getAnswerOption(i), event -> {
                final StatisticsEntity statisticsEntity = new StatisticsEntity();
                final short userId = (short) (currentUser-1);
                statisticsEntity.setQuestionId(question.getId());
                statisticsEntity.setTimestamp(System.currentTimeMillis());
                statisticsEntity.setSelectedAnswer(clickedAnswer);
                final ArrayList<StatisticsEntity> statisticsEntities = this.statisticsEntities.getOrDefault(userId, new ArrayList<>());
                statisticsEntities.add(statisticsEntity);
                this.statisticsEntities.put(userId, statisticsEntities);
                if (correctAnswer == clickedAnswer) {
                    getCurrentPlayer().increasePoints(points);
                    final Notification notification = new Notification(null, 7500, Notification.Position.TOP_START);
                    final Icon icon = LumoIcon.CHECKMARK.create();
                    icon.setColor("var(--lumo-success-color)");
                    icon.setSize("4em");
                    final Div correctState = new Div(new Text("Antwort korrekt"));
                    correctState.getStyle().set("font-weight", "600").set("color", "var(--lumo-success-text-color)");
                    final Span pointsInfo = new Span("+" + points);
                    pointsInfo.getStyle().set("font-size", "var(--lumo-font-size-s)").set("font-weight", "600");
                    final Div info =  new Div(correctState, new Div(new Text("Du hast "), pointsInfo, new Text(" Punkte erhalten.")));
                    final HorizontalLayout layout = new HorizontalLayout(icon, info);
                    layout.setAlignItems(Alignment.CENTER);
                    notification.add(layout);
                    notification.open();
                } else {
                    final Notification notification = new Notification(null, 20000, Notification.Position.TOP_START);
                    final Icon icon = LumoIcon.CROSS.create();
                    icon.setColor("var(--lumo-error-color)");
                    icon.setSize("4em");
                    final Div correctState = new Div(new Text("Antwort inkorrekt"));
                    correctState.getStyle().set("font-weight", "600").set("color", "var(--lumo-error-text-color)");
                    final Span answerInfo = new Span(question.getAnswerOption(question.getCorrectAnswerId()));
                    answerInfo.getStyle().set("font-size", "var(--lumo-font-size-s)").set("font-weight", "600");
                    final Div info =  new Div(correctState, new Div(answerInfo, new Text(" ist die korrekte Antwort.")));
                    final HorizontalLayout layout = new HorizontalLayout(icon, info);
                    layout.setAlignItems(Alignment.CENTER);
                    notification.add(layout);
                    notification.open();
                }
                dialog.close();
                nextPlayer();
            });
            buttonLayout.add(answerButton);
        }

        dialog.add(buttonLayout);
        dialog.open();
        buttonsAvailable--;
    }

    private void showGroupSizeDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Gruppengröße auswählen");
        HorizontalLayout dialogLayout = new HorizontalLayout();

        for (int i = 2; i <= 4; i++) {
            int finalI = i;
            Button playersButton = new Button(i + " Spieler", event -> {
                groupSize = (short) finalI;
                dialog.close();
                showNameDialog();
            });
            dialogLayout.add(playersButton);
        }

        dialog.add(dialogLayout);
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.open();
    }

    private void showNameDialog() {
        AtomicInteger playerId = new AtomicInteger(1);
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Gruppenmitglied " + playerId.get());
        HorizontalLayout dialogLayout = new HorizontalLayout();
        TextField firstName = new TextField("Vorname", "Hans");
        TextField lastName = new TextField("Nachname", "Peter");

        Button confirmButton = new Button("Bestätigen", event -> {
            if (firstName.getOptionalValue().isPresent() && lastName.getOptionalValue().isPresent()) {
                switch (playerId.get()) {
                    case 1 -> users[0] = new UserEntity(firstName.getValue(), lastName.getValue(), groupSize);
                    case 2 -> {
                        users[1] = new UserEntity(firstName.getValue(), lastName.getValue(), groupSize);
                        if (groupSize == 2) {
                            dialog.close();
                            showGame();
                            return;
                        }
                    }
                    case 3 -> {
                        users[2] = new UserEntity(firstName.getValue(), lastName.getValue(), groupSize);
                        if (groupSize == 3) {
                            dialog.close();
                            showGame();
                            return;
                        }
                    }
                    case 4 -> {
                        users[3] = new UserEntity(firstName.getValue(), lastName.getValue(), groupSize);
                        dialog.close();
                        showGame();
                        return;
                    }
                }
                dialog.setHeaderTitle("Gruppenmitglied " + playerId.incrementAndGet());
                firstName.clear();
                lastName.clear();
            }
        });

        dialogLayout.add(firstName, lastName);
        dialog.add(dialogLayout);
        dialog.getFooter().add(confirmButton);
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.open();
    }

    private void showRoundResultDialog() {
        Dialog dialog = new Dialog();
        List<UserEntity> sortedUserList = Stream.of(users)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(UserEntity::getPoints).reversed())
                .toList();

        dialog.setHeaderTitle(sortedUserList.get(0).getFirstName() + " hat diese Runde gewonnen!");

        for (UserEntity user : sortedUserList) {
            HorizontalLayout userLayout = new HorizontalLayout();
            Span pointsSpan = new Span(user.getPoints() + " Punkte");
            pointsSpan.getElement().getThemeList().add("badge small");
            Text userText = new Text(user.getFullName());
            userLayout.add(pointsSpan, userText);
            dialog.add(userLayout);
        }

        dialog.getFooter().add(new Button("Schließen", event -> {
            dialog.close();
            UI.getCurrent().navigate("");
        }));

        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.open();
    }
}