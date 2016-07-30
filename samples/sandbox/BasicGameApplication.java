
//        input.addAction(new UserAction("Cinematic") {
//            @Override
//            protected void onActionBegin() {
//                Viewport viewport = getGameScene().getViewport();
//
//                Point2D[] points = {
//                        new Point2D(1500, 200)
////                        new Point2D(800, -200),
////                        new Point2D(230, 0),
////                        Point2D.ZERO
//                };
//
//                Timeline timeline = new Timeline();
//
//                for (Point2D p : points) {
//                    KeyValue kv = new KeyValue(viewport.xProperty(), p.getX());
//                    KeyValue kv2 = new KeyValue(viewport.yProperty(), p.getY());
//
//                    KeyFrame frame = new KeyFrame(Duration.seconds(3), kv, kv2);
//                    timeline.getKeyFrames().add(frame);
//                }
//
//                timeline.play();
//            }
//        }, KeyCode.K);
//    }