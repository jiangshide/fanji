import 'package:fanji/tab/Tab.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(Fanji(0));
}

class Fanji extends StatefulWidget {
  int themeIndex;

  Fanji(this.themeIndex);

  @override
  State<StatefulWidget> createState() => _FanjiState();

  // Future<int> getTheme() async {
  //   SharedPreferences sharedPreferences = await SharedPreferences.getInstance();
  //   int themeIndex = sharedPreferences.getInt("themeIndex");
  //   return themeIndex;
  // }
}

class _FanjiState extends State<Fanji> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: "梵记",
      theme: ThemeData(
          primarySwatch: Colors.red,
          visualDensity: VisualDensity.adaptivePlatformDensity),
      home: TabWidget(),
    );
  }
}
