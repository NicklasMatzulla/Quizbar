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

import lombok.Getter;

public class Question {

    @Getter
    private final Category category;
    @Getter
    private final Long id;
    @Getter
    private final int points;
    @Getter
    private final String question;
    private final String answerOptionOne;
    private final String answerOptionTwo;
    private final String answerOptionThree;
    private final String answerOptionFour;
    @Getter
    private final short correctAnswerId;

    public Question(Category category, long id, int points, String question, String answerOptionOne,
                    String answerOptionTwo, String answerOptionThree,
                    String answerOptionFour, short correctAnswerId) {
        this.category = category;
        this.id = id;
        this.points = points;
        this.question = question;
        this.answerOptionOne = answerOptionOne;
        this.answerOptionTwo = answerOptionTwo;
        this.answerOptionThree = answerOptionThree;
        this.answerOptionFour = answerOptionFour;
        this.correctAnswerId = correctAnswerId;
    }

    public String getAnswerOption(final int answerOption) {
        switch (answerOption) {
            case 1 -> {
                return this.answerOptionOne;
            }
            case 2 -> {
                return this.answerOptionTwo;
            }
            case 3 -> {
                return this.answerOptionThree;
            }
            case 4 -> {
                return this.answerOptionFour;
            }
        }
        return null;
    }

    public enum Category {
        GENERAL,
        LITERATURE,
        HEALTH,
        ECONOMICS,
        SPORTS
    }

}
