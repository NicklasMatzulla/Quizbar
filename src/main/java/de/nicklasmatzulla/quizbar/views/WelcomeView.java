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
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import de.nicklasmatzulla.quizbar.jpa.entity.UserEntity;
import de.nicklasmatzulla.quizbar.jpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageTitle("Willkommen")
@Route(value = "")
public class WelcomeView extends VerticalLayout {

    @Autowired
    public WelcomeView(final UserRepository repository) {
        setSpacing(false);

        Image img = new Image("images/smoothie.png", "Smoothie");
        img.setWidth("200px");
        add(img);

        H2 header = new H2("Gratis Smoothies 🤗");
        header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
        add(header);

        add(new Paragraph("Spiele gegen deine Freunde und erhalte mit etwas Glück eine großartige Belohnung."));

        final HorizontalLayout buttonsLayout = new HorizontalLayout();
        final Button playButton = new Button("Spiele jetzt", event -> UI.getCurrent().navigate("game"));
        playButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        final Button statisticsButton = new Button("Top Spieler", event -> {
            final Dialog statisticsDialog = new Dialog("Top Spieler");
            final TabSheet tabSheet = new TabSheet();
            final Tab groupSize2 = new Tab("2er Gruppen");
            final Tab groupSize3 = new Tab("3er Gruppen");
            final Tab groupSize4 = new Tab("4er Gruppen");

            final List<UserEntity> top2Users = repository.findTop3ByGroupSizeOrderByPointsDesc(2);
            final List<UserEntity> top3Users = repository.findTop3ByGroupSizeOrderByPointsDesc(3);
            final List<UserEntity> top4Users = repository.findTop3ByGroupSizeOrderByPointsDesc(4);

            final Div top2UsersDiv = new Div();
            for (int i = 0; i < top2Users.size(); i++) {
                final UserEntity user = top2Users.get(i);
                final HorizontalLayout userLayout = new HorizontalLayout();
                final Span pointsSpan = new Span(user.getPoints() + " Punkte");
                pointsSpan.getElement().getThemeList().add("badge small");
                final Text userText = new Text(user.getFullName());
                userLayout.add(pointsSpan, userText);
                top2UsersDiv.add(userLayout);
            }
            if (top2Users.isEmpty())
                top2UsersDiv.add(new Text("Es sind noch keine Statistiken vorhanden!"));
            tabSheet.add(groupSize2, top2UsersDiv);

            final Div top3UsersDiv = new Div();
            for (int i = 0; i < top3Users.size(); i++) {
                final UserEntity user = top3Users.get(i);
                final HorizontalLayout userLayout = new HorizontalLayout();
                final Span pointsSpan = new Span(user.getPoints() + " Punkte");
                pointsSpan.getElement().getThemeList().add("badge small");
                final Text userText = new Text(user.getFullName());
                userLayout.add(pointsSpan, userText);
                top3UsersDiv.add(userLayout);
            }
            if (top3Users.isEmpty())
                top3UsersDiv.add(new Text("Es sind noch keine Statistiken vorhanden!"));
            tabSheet.add(groupSize3, top3UsersDiv);

            final Div top4UsersDiv = new Div();
            for (int i = 0; i < top4Users.size(); i++) {
                final UserEntity user = top4Users.get(i);
                final HorizontalLayout userLayout = new HorizontalLayout();
                final Span pointsSpan = new Span(user.getPoints() + " Punkte");
                pointsSpan.getElement().getThemeList().add("badge small");
                final Text userText = new Text(user.getFullName());
                userLayout.add(pointsSpan, userText);
                top4UsersDiv.add(userLayout);
            };
            if (top4Users.isEmpty())
                top4UsersDiv.add(new Text("Es sind noch keine Statistiken vorhanden!"));
            tabSheet.add(groupSize4, top4UsersDiv);

            statisticsDialog.add(tabSheet);
            final Button closeButton = new Button("Schließen", ignored -> statisticsDialog.close());
            closeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            statisticsDialog.getFooter().add(closeButton);
            statisticsDialog.open();
        });
        statisticsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonsLayout.add(playButton, statisticsButton);
        add(buttonsLayout);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

}