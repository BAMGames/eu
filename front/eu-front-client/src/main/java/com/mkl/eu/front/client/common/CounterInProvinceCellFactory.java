package com.mkl.eu.front.client.common;

import com.mkl.eu.client.service.vo.board.Counter;
import com.mkl.eu.front.client.main.GlobalConfiguration;
import com.mkl.eu.front.client.main.UIUtil;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 * Cell factory for counter in combo box with province display.
 *
 * @author MKL.
 */
public class CounterInProvinceCellFactory implements Callback<ListView<Counter>, ListCell<Counter>> {

    /** {@inheritDoc} */
    @Override
    public ListCell<Counter> call(ListView<Counter> param) {
        return new ListCell<Counter>() {
            @Override
            protected void updateItem(Counter item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    HBox hBox = new HBox();
                    setGraphic(hBox);
                    hBox.getChildren().addAll(UIUtil.getImage(item), new Label(" - " + GlobalConfiguration.getMessage(item.getOwner().getProvince())));
                }
            }
        };
    }
}
