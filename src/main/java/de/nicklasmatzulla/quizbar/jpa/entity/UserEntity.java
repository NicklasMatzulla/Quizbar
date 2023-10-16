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

package de.nicklasmatzulla.quizbar.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "users")
public class UserEntity {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String firstName;

    @Getter
    private String lastName;

    @Getter
    private int groupSize;

    @Getter
    private long points = 0;

    public UserEntity(final String firstName, final String lastName, final int groupSize) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.groupSize = groupSize;
    }

    public UserEntity() {
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public void increasePoints(final int amount) {
        this.points += amount;
    }
}
