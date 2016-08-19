function onUpdate(/*Entity*/ self, /*double*/ tpf) {

    self.rotateBy(2.0 * 60 * tpf);  // rotate by 2 degrees per frame (or 120 degrees a second)

    java.lang.System.out.println("Printed from JS, tick: " + APP.getTick());
}