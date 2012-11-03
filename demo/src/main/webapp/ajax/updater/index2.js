function update(btn) {
      if (atimer.scheduled && !atimer.cancelled) {
        atimer.cancel();
        btn.setText("开始自动更新（间隔2秒）");
      } else {
        atimer.schedule();
        btn.setText("停止自动更新");
      }
      return false;
}