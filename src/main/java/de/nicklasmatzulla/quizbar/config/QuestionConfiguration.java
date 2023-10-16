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

package de.nicklasmatzulla.quizbar.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class QuestionConfiguration {

    @Getter
    private static QuestionConfiguration instance;
    private final JsonElement configuration;
    private final ArrayList<Question> questions = new ArrayList<>();

    public QuestionConfiguration() {
        QuestionConfiguration.instance = this;
        try {
            final File configurationFile = new File("questions.json");
            this.configuration = JsonParser.parseReader(new InputStreamReader(new FileInputStream(configurationFile), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        processQuestions();
    }

    public Question findNextQuestion(final Question.Category category, final int points) {
        final List<Question> possibleQuestions = this.questions.stream()
                .filter(question -> question.getCategory() == category)
                .filter(question -> question.getPoints() == points)
                .toList();
        final Random random = new Random();
        final int randomIndex = random.nextInt(possibleQuestions.size());
        return possibleQuestions.get(randomIndex);
    }

    public Question findQuestionById(final long id) {
        return this.questions.stream()
                .filter(question -> question.getId() == id)
                .findAny()
                .orElseThrow();
    }

    private void processQuestions() {
        final JsonObject json = this.configuration.getAsJsonObject();
        for (final String category : new String[]{"general", "literature", "health", "economics", "sports"}) {
            json.getAsJsonArray(category).forEach(element -> {
                final JsonObject object = element.getAsJsonObject();
                final long id = object.get("id").getAsLong();
                final int points = object.get("points").getAsInt();
                final String question = object.get("question").getAsString();
                final JsonObject answerOptions = object.get("answer_options").getAsJsonObject();
                final String answerOptionOne = answerOptions.get("1").getAsString();
                final String answerOptionTwo = answerOptions.get("2").getAsString();
                final String answerOptionThree = answerOptions.get("3").getAsString();
                final String answerOptionFour = answerOptions.get("4").getAsString();
                final short correctAnswerId = object.get("correct_answer_id").getAsShort();
                this.questions.add(new Question(Question.Category.valueOf(category.toUpperCase()), id, points, question,
                        answerOptionOne, answerOptionTwo, answerOptionThree, answerOptionFour, correctAnswerId));
            });
        }
    }

}
