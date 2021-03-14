import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import 'constants.dart';

class HomdeDetail extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _HomeDetailState();
}

class _HomeDetailState extends State<HomdeDetail> {
  final CategoriesScroller categoriesScroller = CategoriesScroller();
  ScrollController controller = ScrollController();
  bool closeTopContainer = false;
  double topContainer = 0;

  List<Widget> itemsData = Datas().getPostsData();

  @override
  void initState() {
    super.initState();
    controller.addListener(() {
      double value = controller.offset / 119;
      setState(() {
        topContainer = value;
        closeTopContainer = controller.offset > 50;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    final Size size = MediaQuery.of(context).size;
    final double categoryHeight = size.height * 0.30;
    return SafeArea(
        child: Scaffold(
      backgroundColor: Colors.white,
      body: Container(
        height: size.height,
        child: Column(
          children: [
            const SizedBox(
              height: 10,
            ),
            AnimatedOpacity(
              opacity: closeTopContainer ? 0 : 1,
              duration: const Duration(microseconds: 200),
              child: AnimatedContainer(
                duration: const Duration(microseconds: 200),
                width: size.width,
                alignment: Alignment.topCenter,
                height: closeTopContainer ? 0 : categoryHeight,
                child: categoriesScroller,
              ),
            ),
            Expanded(
              child: ListView.builder(
                controller: controller,
                itemCount: itemsData.length,
                physics: BouncingScrollPhysics(),
                itemBuilder: (context, index) {
                  double scale = 1.0;
                  if (topContainer > 0.5) {
                    scale = index + 0.5 - topContainer;
                    if (scale < 0) {
                      scale = 0;
                    } else if (scale > 1) {
                      scale = 1;
                    }
                  }
                  return Opacity(
                    opacity: scale,
                    child: Transform(
                      transform: Matrix4.identity()..scale(scale, scale),
                      alignment: Alignment.bottomCenter,
                      child: Align(
                        heightFactor: 0.7,
                        alignment: Alignment.topCenter,
                        child: itemsData[index],
                      ),
                    ),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    ));
  }
}

class CategoriesScroller extends StatelessWidget {
  // const CategoriesScroller();

  List<String> titles = [
    "新闻",
    "电影",
    "福星",
    "新闻",
    "电影",
    "福星",
    "新闻",
    "电影",
    "福星",
    "新闻",
    "电影",
    "福星"
  ];
  List<String> contents = [
    "今天新闻很棒！",
    "阿凡达再次上映！",
    "越努力越幸运！",
    "今天新闻很棒！",
    "阿凡达再次上映！",
    "越努力越幸运！",
    "今天新闻很棒！",
    "阿凡达再次上映！",
    "越努力越幸运！",
    "今天新闻很棒！",
    "阿凡达再次上映！",
    "越努力越幸运！"
  ];

  @override
  Widget build(BuildContext context) {
    final double categoryHeight =
        MediaQuery.of(context).size.height * 0.30 - 50;
    return SingleChildScrollView(
      physics: BouncingScrollPhysics(),
      scrollDirection: Axis.horizontal,
      child: Container(
        margin: const EdgeInsets.symmetric(vertical: 20, horizontal: 20),
        child: FittedBox(
          fit: BoxFit.fill,
          alignment: Alignment.topCenter,
          child: Row(
            children: getContainers(titles, contents, categoryHeight),
          ),
        ),
      ),
    );
  }

  List<Container> getContainers(
      List<String> titles, List<String> contents, double categoryHeight) {
    List<Container> containers = [];
    for (var i = 0; i < titles.length; i++) {
      containers.add(getContainer(titles[i], contents[i], categoryHeight));
    }
    return containers;
  }

  Container getContainer(String title, String content, double categoryHeight) {
    var container = Container(
      width: 150,
      margin: EdgeInsets.only(right: 20),
      height: categoryHeight,
      decoration: BoxDecoration(
          color: Colors.lightBlueAccent.shade400,
          borderRadius: BorderRadius.all(Radius.circular(20.0))),
      child: Padding(
        padding: const EdgeInsets.all(12.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              title,
              style: TextStyle(
                  fontSize: 25,
                  color: Colors.white,
                  fontWeight: FontWeight.bold),
            ),
            SizedBox(
              height: 10,
            ),
            Text(
              content,
              style: TextStyle(fontSize: 16, color: Colors.white),
            ),
          ],
        ),
      ),
    );
    return container;
  }
}
