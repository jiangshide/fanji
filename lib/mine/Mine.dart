import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class Mine extends StatefulWidget {
  State<StatefulWidget> createState() {
    return _MineState();
  }
}

class _MineState extends State<Mine> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Text(
          "Mine",
          style: TextStyle(
              fontSize: 40, color: Colors.green, fontStyle: FontStyle.italic),
        ),
      ),
    );
  }
}
