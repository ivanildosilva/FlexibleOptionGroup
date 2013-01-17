/*
 * Copyright 2009 IT Mill Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.gov.sp.saovicente;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import org.vaadin.hene.flexibleoptiongroup.FlexibleOptionGroup;

/**
 * The Application's "main" class
 */
import java.util.Iterator;

import com.vaadin.Application;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import org.vaadin.hene.flexibleoptiongroup.FlexibleOptionGroupItemComponent;

/**
 * @author Henri Kerola / Vaadin Ltd
 * 
 */
public class MyVaadinApplication extends Application {

	private static final String CAPTION_PROPERTY = "caption";
	private static final String ICON_PROPERTY = "icon";
	private static final String SELECTION_PROPERTY = "selection";

	private static final String[] DOCUMENTS = new String[] { "Word",
			"document-doc.png", "Image", "document-image.png", "PDF",
			"document-pdf.png", "PowerPoint", "document-ppt.png", "Text",
			"document-txt.png", "Web", "document-web.png", "Excel",
			"document-xsl.png" };

	private FlexibleOptionGroupPropertyEditor propertyEditor;

	@Override
	public void init() {
		Window mainWindow = new Window("FlexibleOptionGroup");
		setMainWindow(mainWindow);

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		mainLayout.setMargin(true);
		mainWindow.setContent(mainLayout);

		Label headerLabel = new Label("FlexibleOptionGroup");
		headerLabel.setStyleName(Reindeer.LABEL_H1);
		mainLayout.addComponent(headerLabel);

		final TabSheet ts = new TabSheet();
		ts.setSizeFull();
		ts.addComponent(new GridLayoutTab());
		ts.addComponent(new TableExampleTab());
		ts.addComponent(new HorizontalOptionGroupTab());
		ts.addComponent(new AbsoluteLayoutTab());
		mainLayout.addComponent(ts);
		mainLayout.setExpandRatio(ts, 1);

		propertyEditor = new FlexibleOptionGroupPropertyEditor();
		propertyEditor.refresh((AbstractTab) ts.getSelectedTab());
		mainLayout.addComponent(propertyEditor);

		ts.addListener(new SelectedTabChangeListener() {

			public void selectedTabChange(SelectedTabChangeEvent event) {
				propertyEditor.refresh((AbstractTab) ts.getSelectedTab());
			}
		});
	}

	private static Container createTestContainer() {
		IndexedContainer cont = new IndexedContainer();
		cont.addContainerProperty(CAPTION_PROPERTY, String.class, null);
		cont.addContainerProperty(ICON_PROPERTY, Resource.class, null);

		for (int i = 0; i < DOCUMENTS.length; i++) {
			String name = DOCUMENTS[i++];
			String id = DOCUMENTS[i];
			Item item = cont.addItem(id);
			valuateTestContainerItem(item, name, id);

		}
		return cont;
	}

	private static void valuateTestContainerItem(Item item, String name,
			String iconName) {
		item.getItemProperty(CAPTION_PROPERTY).setValue(name);
		item.getItemProperty(ICON_PROPERTY).setValue(
				new ThemeResource("../runo/icons/16/" + iconName));
	}

	public static Label createCaptionLabel(FlexibleOptionGroupItemComponent fog) {
		Label captionLabel = new Label();
		captionLabel.setData(fog);
		captionLabel.setIcon(fog.getIcon());
		captionLabel.setCaption(fog.getCaption());
		captionLabel.setWidth(null);
		return captionLabel;
	}

	private static abstract class AbstractTab extends VerticalLayout {

		protected FlexibleOptionGroup flexibleOptionGroup;

		protected LayoutClickListener layoutClickListener = new LayoutClickListener() {

			public void layoutClick(LayoutClickEvent event) {
				FlexibleOptionGroupItemComponent c = null;
				boolean allowUnselection = flexibleOptionGroup.isMultiSelect();
				if (event.getChildComponent() instanceof FlexibleOptionGroupItemComponent) {
					c = (FlexibleOptionGroupItemComponent) event
							.getChildComponent();
				} else if (event.getChildComponent() instanceof AbstractComponent) {
					Object data = ((AbstractComponent) event
							.getChildComponent()).getData();
					if (data instanceof FlexibleOptionGroupItemComponent) {
						c = (FlexibleOptionGroupItemComponent) data;
					}
					if (event.getChildComponent() instanceof HorizontalLayout) {
						allowUnselection = false;
					}
				}
				if (c != null) {
					Object itemId = c.getItemId();
					if (flexibleOptionGroup.isSelected(itemId)
							&& allowUnselection) {
						flexibleOptionGroup.unselect(itemId);
					} else {
						flexibleOptionGroup.select(itemId);
					}
				}
			}
		};

		public AbstractTab(String caption) {
			setCaption(caption);
			setMargin(true);
			flexibleOptionGroup = new FlexibleOptionGroup(createTestContainer());
			flexibleOptionGroup.setItemCaptionPropertyId(CAPTION_PROPERTY);
			flexibleOptionGroup.setItemIconPropertyId(ICON_PROPERTY);
		}
	}

	private static class GridLayoutTab extends AbstractTab {

		private GridLayout layout;

		public GridLayoutTab() {
			super("GridLayout");

			Item otherItem = flexibleOptionGroup.addItem("other");
			valuateTestContainerItem(otherItem, "other", "document.png");

			layout = new GridLayout(2, 1);
			layout.setWidth("100%");
			layout.setColumnExpandRatio(1, 1);
			layout.addListener(layoutClickListener);
			addComponent(layout);

			for (Iterator<FlexibleOptionGroupItemComponent> iter = flexibleOptionGroup
					.getItemComponentIterator(); iter.hasNext();) {
				FlexibleOptionGroupItemComponent c = iter.next();
				layout.addComponent(c);
				if ("other".equals(c.getItemId())) {
					layout.setComponentAlignment(c, Alignment.MIDDLE_CENTER);
					HorizontalLayout otherLayout = createOtherItemLayout(otherItem);
					otherLayout.setData(c);
					layout.addComponent(otherLayout);
				} else {
					layout.addComponent(createCaptionLabel(c));
				}
				layout.newLine();
			}

		}

		private HorizontalLayout createOtherItemLayout(Item otherItem) {
			HorizontalLayout otherLayout = new HorizontalLayout();
			Label otherIcon = new Label();
			otherIcon.setWidth("16px");
			otherIcon.setIcon((Resource) otherItem.getItemProperty(
					ICON_PROPERTY).getValue());
			otherLayout.addComponent(otherIcon);
			otherLayout.setComponentAlignment(otherIcon,
					Alignment.MIDDLE_CENTER);
			TextField otherTextField = new TextField();
			otherTextField.setInputPrompt("Other");
			otherLayout.addComponent(otherTextField);
			return otherLayout;
		}

	}

	private static class TableExampleTab extends AbstractTab {

		public TableExampleTab() {
			super("Table");

			final Table table = new Table(null,
					flexibleOptionGroup.getContainerDataSource());

			flexibleOptionGroup = new FlexibleOptionGroup(createTestContainer()) {
				public void setImmediate(boolean immediate) {
					super.setImmediate(immediate);
					table.setImmediate(true);
				}

				public void setMultiSelect(boolean multiSelect) {
					super.setMultiSelect(multiSelect);
					table.setMultiSelect(multiSelect);
				}

				public void setEnabled(boolean enabled) {
					super.setEnabled(enabled);
					table.setEnabled(enabled);
				}

				public void setReadOnly(boolean readOnly) {
					super.setReadOnly(readOnly);
					table.setReadOnly(readOnly);
				}
			};
			flexibleOptionGroup.setItemCaptionPropertyId(CAPTION_PROPERTY);
			flexibleOptionGroup.setItemIconPropertyId(ICON_PROPERTY);

			flexibleOptionGroup.setImmediate(true);
			flexibleOptionGroup
					.setPropertyDataSource(new ObjectProperty<Object>(null,
							Object.class));

			table.setSelectable(true);
			table.setPropertyDataSource(flexibleOptionGroup
					.getPropertyDataSource());
			table.addGeneratedColumn(SELECTION_PROPERTY, new ColumnGenerator() {
				public Component generateCell(Table source, Object itemId,
						Object columnId) {
					return flexibleOptionGroup.getItemComponent(itemId);
				}
			});
			table.setRowHeaderMode(Table.ROW_HEADER_MODE_HIDDEN);
			table.setItemIconPropertyId(ICON_PROPERTY);
			table.setVisibleColumns(new Object[] { SELECTION_PROPERTY,
					CAPTION_PROPERTY });
			table.setColumnHeader(SELECTION_PROPERTY, "");
			addComponent(table);
		}
	}

	private static class HorizontalOptionGroupTab extends AbstractTab {

		private HorizontalLayout layout;

		public HorizontalOptionGroupTab() {
			super("HorizontalLayout");

			layout = new HorizontalLayout();
			layout.addListener(layoutClickListener);
			addComponent(layout);

			for (Iterator<FlexibleOptionGroupItemComponent> iter = flexibleOptionGroup
					.getItemComponentIterator(); iter.hasNext();) {
				FlexibleOptionGroupItemComponent c = iter.next();
				layout.addComponent(c);
				layout.addComponent(createCaptionLabel(c));
			}

		}

	}

	private static class AbsoluteLayoutTab extends AbstractTab {

		private AbsoluteLayout layout;

		public AbsoluteLayoutTab() {
			super("AbsoluteLayout");
			setSizeFull();

			layout = new AbsoluteLayout();
			layout.addListener(layoutClickListener);
			layout.setSizeFull();
			addComponent(layout);

			int x = 10;
			int y = 10;
			for (Iterator<FlexibleOptionGroupItemComponent> iter = flexibleOptionGroup
					.getItemComponentIterator(); iter.hasNext();) {
				FlexibleOptionGroupItemComponent c = iter.next();
				layout.addComponent(c, "top: " + y + "; left: " + x);
				layout.addComponent(createCaptionLabel(c), "top: " + (y + 15)
						+ "; left: " + (x + 20));
				x += 20;
				y += 20;
			}
		}

	}

	private static class FlexibleOptionGroupPropertyEditor extends
			VerticalLayout implements ClickListener {

		private AbstractTab tab;

		private CheckBox immediateCheckBox = new CheckBox("Immediate", this);
		private CheckBox enableCheckBox = new CheckBox("Enabled", this);
		private CheckBox readOnlyCheckBox = new CheckBox("Read-only", this);
		private CheckBox multiSelectCheckBox = new CheckBox("Multi-select",
				this);

		public FlexibleOptionGroupPropertyEditor() {
			immediateCheckBox.setImmediate(true);
			addComponent(immediateCheckBox);
			enableCheckBox.setImmediate(true);
			addComponent(enableCheckBox);
			readOnlyCheckBox.setImmediate(true);
			addComponent(readOnlyCheckBox);
			multiSelectCheckBox.setImmediate(true);
			addComponent(multiSelectCheckBox);
		}

		public void refresh(AbstractTab tab) {
			this.tab = tab;
			FlexibleOptionGroup fop = tab.flexibleOptionGroup;
			immediateCheckBox.setValue(fop.isImmediate());
			enableCheckBox.setValue(fop.isEnabled());
			readOnlyCheckBox.setValue(fop.isReadOnly());
			multiSelectCheckBox.setValue(fop.isMultiSelect());
		}

		public void buttonClick(ClickEvent event) {
			FlexibleOptionGroup fop = tab.flexibleOptionGroup;
			if (immediateCheckBox == event.getButton()) {
				fop.setImmediate(immediateCheckBox.booleanValue());
			} else if (enableCheckBox == event.getButton()) {
				fop.setEnabled(enableCheckBox.booleanValue());
			} else if (readOnlyCheckBox == event.getButton()) {
				fop.setReadOnly(readOnlyCheckBox.booleanValue());
			} else if (multiSelectCheckBox == event.getButton()) {
				fop.setMultiSelect(multiSelectCheckBox.booleanValue());
			}
		}
	}

}