function update(btn) {
      if (updater.scheduled && !updater.cancelled) {
        updater.cancel();
        btn.setText("Start Update");
      } else {
        updater.schedule();
        btn.setText("Stop Update");
      }
      return false;
}