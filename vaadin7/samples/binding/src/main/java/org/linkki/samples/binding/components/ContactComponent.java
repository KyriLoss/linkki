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
package org.linkki.samples.binding.components;

import java.util.function.Consumer;

import org.linkki.core.binding.Binder;
import org.linkki.core.binding.BindingContext;
import org.linkki.core.ui.section.AbstractSection;
import org.linkki.core.ui.section.PmoBasedSectionFactory;
import org.linkki.samples.binding.model.Address;
import org.linkki.samples.binding.model.Contact;
import org.linkki.samples.binding.pmo.AddressPmo;
import org.linkki.samples.binding.pmo.ButtonsSectionPmo;
import org.linkki.samples.binding.pmo.ChildrenSectionPmo;
import org.linkki.samples.binding.pmo.ContactSectionPmo;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class ContactComponent extends Panel {

    private static final long serialVersionUID = 1L;

    private final PmoBasedSectionFactory sectionFactory = new PmoBasedSectionFactory();

    private final BindingContext bindingContext;

    private final ContactSectionPmo contactPmo;
    private final AddressPmo addressPmo;
    private final ChildrenSectionPmo childrenSectionPmo;
    private final ButtonsSectionPmo buttonsSectionPmo;

    private final Consumer<Contact> persistAction;

    public ContactComponent(Consumer<Contact> persistAction, BindingContext bindingContext) {

        this.bindingContext = bindingContext;
        this.persistAction = persistAction;

        Contact contact = new Contact();
        this.contactPmo = new ContactSectionPmo(contact);
        this.addressPmo = new AddressPmo(contact.getAddress());
        this.childrenSectionPmo = new ChildrenSectionPmo(contact);

        this.buttonsSectionPmo = new ButtonsSectionPmo(this::canSave, this::save, this::reset);

        setCaption("Create / Edit Contact");
        createContent();
    }

    public void editContact(Contact contact) {
        resetPmos(contact);
    }

    private void createContent() {

        AbstractSection contactSection = sectionFactory.createSection(contactPmo, bindingContext);

        AddressFields addressFields = new AddressFields();
        AddressComponent addressComponent = new AddressComponent(addressFields);

        new Binder(addressFields, addressPmo).setupBindings(bindingContext);

        AbstractSection childrenSection = sectionFactory.createSection(childrenSectionPmo, bindingContext);
        AbstractSection buttonsSection = sectionFactory.createSection(buttonsSectionPmo, bindingContext);
        buttonsSection.iterator()
                .forEachRemaining(c -> buttonsSection.setComponentAlignment(c, Alignment.MIDDLE_RIGHT));

        VerticalLayout wrapperLayout = new VerticalLayout(contactSection, addressComponent, childrenSection,
                buttonsSection);
        wrapperLayout.setMargin(true);
        setContent(wrapperLayout);
    }

    private boolean canSave() {
        return contactPmo.isInputValid() && addressPmo.isInputValid();
    }

    private void save() {
        Address address = addressPmo.getAddressInEdit();
        Contact contact = contactPmo.getContact();
        contact.setAddress(address);

        persistAction.accept(contact);

        reset();
    }

    private void reset() {
        resetPmos(new Contact());
    }

    private void resetPmos(Contact contact) {
        contactPmo.reset(contact);
        childrenSectionPmo.reset(contact);
        addressPmo.reset(contact.getAddress());

        bindingContext.modelChanged();
    }

}