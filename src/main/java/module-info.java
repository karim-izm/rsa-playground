module ssi.master.rsaplayground {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;


    opens ssi.master.rsaplayground to javafx.fxml;
    opens ssi.master.rsaplayground.controllers;
    exports ssi.master.rsaplayground;
    exports ssi.master.rsaplayground.controllers;
}