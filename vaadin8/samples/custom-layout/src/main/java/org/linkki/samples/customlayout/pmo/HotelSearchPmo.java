/*
 * Copyright Faktor Zehn GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */

package org.linkki.samples.customlayout.pmo;

import java.time.LocalDate;

import org.linkki.core.ui.element.annotation.UIButton;
import org.linkki.core.ui.element.annotation.UIDateField;
import org.linkki.core.ui.element.annotation.UIIntegerField;
import org.linkki.samples.customlayout.annotation.UIHorizontalLayout;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Notification;

// tag::hotelsearch[]
@UIHorizontalLayout
public class HotelSearchPmo {

    private int noOfGuests;
    private LocalDate arrival;
    private LocalDate depature;

    @UIIntegerField(position = 10, label = "Number of Guests")
    public int getNoOfGuests() {
        return noOfGuests;
    }

    public void setNoOfGuests(int noOfGuests) {
        this.noOfGuests = noOfGuests;
    }

    // end::hotelsearch[]

    @UIDateField(position = 20, label = "Date of Arrival")
    public LocalDate getArrival() {
        return arrival;
    }

    public void setArrival(LocalDate arrival) {
        this.arrival = arrival;
    }

    @UIDateField(position = 30, label = "Date of Depature")
    public LocalDate getDepature() {
        return depature;
    }

    public void setDepature(LocalDate depature) {
        this.depature = depature;
    }

    @UIButton(position = 40, caption = "Send", icon = VaadinIcons.PAPERPLANE, showIcon = true)
    public void send() {
        int numberOfBeds = (int)(Math.random() * 10);
        Notification.show(
                          String.format("We have %d bed%s available!", numberOfBeds, numberOfBeds == 1 ? "" : "s"),
                          "Thank you for your request!",
                          Notification.Type.TRAY_NOTIFICATION);
    }
}
