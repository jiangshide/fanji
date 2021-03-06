import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class Home extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _HomeState();
  }
}

class _HomeState extends State<Home> {
  bool isLoadingImg = true;
  String imgUrl =
      "http://zd112.oss-cn-beijing.aliyuncs.com/jpg/compress_mmexport1538795397848.jpg";

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: buildListView(),
      ),
    );
  }

  ListView buildListView() {
    return ListView.separated(
        itemBuilder: (BuildContext context, int index) {
          if (isLoadingImg) {
            return Image.network(
              imgUrl,
              height: 100,
              fit: BoxFit.fitHeight,
            );
          } else {
            return Container(
              child: Text("加载中..."),
            );
          }
        },
        separatorBuilder: (BuildContext context, int index) {
          return new Divider();
        },
        itemCount: 100);
  }
}
