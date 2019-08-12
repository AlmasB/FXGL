open module samples.main {
    requires kotlin.stdlib;
    requires com.almasb.fxgl.all;

    // these are only here while being developed
    requires com.almasb.fxgl.trade;
    requires com.almasb.fxgl.cutscene;

    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.module.kotlin;
    requires jackson.annotations;
}