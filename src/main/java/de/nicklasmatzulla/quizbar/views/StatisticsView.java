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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.nicklasmatzulla.quizbar.config.Question;
import de.nicklasmatzulla.quizbar.config.QuestionConfiguration;
import de.nicklasmatzulla.quizbar.jpa.entity.StatisticsEntity;
import de.nicklasmatzulla.quizbar.jpa.entity.UserEntity;
import de.nicklasmatzulla.quizbar.jpa.repository.StatisticsRepository;
import de.nicklasmatzulla.quizbar.jpa.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Statistiken")
@Route(value = "statistics")
public class StatisticsView extends VerticalLayout {

    @Autowired
    public StatisticsView(@NotNull final UserRepository userRepository, @NotNull final StatisticsRepository statisticsRepository) {
        final QuestionConfiguration questionConfiguration = QuestionConfiguration.getInstance();
        final Grid<UserEntity> grid = new Grid<>(UserEntity.class, false);
        grid.addColumn(UserEntity::getId).setHeader("ID").setSortable(true);
        grid.addColumn(UserEntity::getFirstName).setHeader("Vorname").setSortable(true);
        grid.addColumn(UserEntity::getLastName).setHeader("Nachname").setSortable(true);
        grid.addColumn(UserEntity::getPoints).setHeader("Punkte").setSortable(true);
        grid.addColumn(UserEntity::getGroupSize).setHeader("Gruppengröße").setSortable(true);
        grid.addComponentColumn(user -> {
            final Button showQuestionsButton = new Button("Fragen anzeigen");
            showQuestionsButton.addClickListener(clickEvent -> {
                final Dialog questionsDialog = new Dialog("Fragenübersicht");
                final List<StatisticsEntity> statisticsEntities = statisticsRepository.findAllByUserId(user.getId());
                final Grid<StatisticsEntity> statisticsGrid = new Grid<>(StatisticsEntity.class, false);
                statisticsGrid.addComponentColumn(statisticsEntity -> {
                    final long questionId = statisticsEntity.getQuestionId();
                    final Question question = questionConfiguration.findQuestionById(questionId);
                    return new Text(String.valueOf(question.getPoints()));
                }).setComparator((o1, o2) -> {
                    final long questionId1 = o1.getQuestionId();
                    final long questionId2 = o2.getQuestionId();
                    final Question question1 = questionConfiguration.findQuestionById(questionId1);
                    final Question question2 = questionConfiguration.findQuestionById(questionId2);
                    return Long.compare(question1.getPoints(), question2.getPoints());
                }).setHeader("Punkte");
                statisticsGrid.addComponentColumn(statisticsEntity -> {
                    final long questionId = statisticsEntity.getQuestionId();
                    final Question question = questionConfiguration.findQuestionById(questionId);
                    return new Text(question.getQuestion());
                }).setHeader("Frage").setAutoWidth(true);
                statisticsGrid.addComponentColumn(statisticsEntity -> {
                    final long questionId = statisticsEntity.getQuestionId();
                    final Question question = questionConfiguration.findQuestionById(questionId);
                    return new Text(question.getAnswerOption(statisticsEntity.getSelectedAnswer()));
                }).setHeader("Antwort").setAutoWidth(true);
                statisticsGrid.addComponentColumn(statisticsEntity -> {
                    final long questionId = statisticsEntity.getQuestionId();
                    final Question question = questionConfiguration.findQuestionById(questionId);
                    final short correctAnswer = question.getCorrectAnswerId();
                    return new Text(question.getAnswerOption(correctAnswer));
                }).setHeader("Korrekte Antwort").setAutoWidth(true);
                statisticsGrid.setPartNameGenerator(statisticsEntity -> {
                    final long questionId = statisticsEntity.getQuestionId();
                    final Question question = questionConfiguration.findQuestionById(questionId);
                    final short correctAnswerId = question.getCorrectAnswerId();
                    final short selectedAnswerId = statisticsEntity.getSelectedAnswer();
                    if (correctAnswerId == selectedAnswerId) {
                        return "correct-answer";
                    } else {
                        return "wrong-answer";
                    }
                });
                statisticsGrid.setItems(statisticsEntities);
                questionsDialog.add(statisticsGrid);
                questionsDialog.setWidthFull();
                questionsDialog.open();
            });
            return showQuestionsButton;
        });

        final List<UserEntity> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        grid.setItems(users);
        add(grid);
    }

}
