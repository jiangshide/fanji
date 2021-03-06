import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class Publish extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _PublishState();
  }
}

class _PublishState extends State<Publish> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Text(
          "Publish",
          style: TextStyle(
              fontSize: 40, color: Colors.yellow, fontStyle: FontStyle.normal),
        ),
      ),
    );
  }
}
