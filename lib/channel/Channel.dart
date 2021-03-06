import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class Channel extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _ChannelState();
}

class _ChannelState extends State<Channel> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Text("Channel"),
      ),
    );
  }
}
