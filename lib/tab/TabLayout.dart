import 'package:flutter/cupertino.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';

import 'TabItem.dart';

// ignore: must_be_immutable
class TabLayout extends StatefulWidget {
  final List<TabItem> items;
  TabController controller;
  final bool isScrollable;
  final Color indicatorColor;
  final double indicatorWeight;
  final EdgeInsetsGeometry indicatorPadding;
  final Decoration indicator;
  final TabBarIndicatorSize indicatorSize;
  final Color lableColor;
  final Color unSelectedLabelColor;
  final TextStyle labelStyle;
  final EdgeInsetsGeometry labelPadding;
  final TextStyle unSelectedLabelStyle;
  final DragStartBehavior dragStartBehavior;
  final OnTabClickListener onTabClickListener;

  TabLayout(
      {@required this.items,
      this.controller,
      this.isScrollable,
      this.indicatorColor,
      this.indicatorWeight,
      this.indicatorPadding,
      this.indicator,
      this.indicatorSize,
      this.lableColor,
      this.unSelectedLabelColor,
      this.labelStyle,
      this.labelPadding,
      this.unSelectedLabelStyle,
      this.dragStartBehavior,
      @required this.onTabClickListener});

  @override
  State<StatefulWidget> createState() => _TabLayoutState(
      items: items,
      controller: controller,
      isScrollable: isScrollable,
      indicatorColor: indicatorColor,
      indicatorWeight: indicatorWeight,
      indicatorPadding: indicatorPadding,
      indicator: indicator,
      indicatorSize: indicatorSize,
      labelColor: lableColor,
      unSelectedLabelColor: unSelectedLabelColor,
      labelStyle: labelStyle,
      labelPadding: labelPadding,
      unSelectedLabelStyle: unSelectedLabelStyle,
      dragStartBehavior: dragStartBehavior,
      onTabClickListener: onTabClickListener);
}

class _TabLayoutState extends State<TabLayout>
    with SingleTickerProviderStateMixin {
  final List<TabItem> items;
  TabController controller;
  final bool isScrollable;
  final Color indicatorColor;
  final double indicatorWeight;
  final EdgeInsetsGeometry indicatorPadding;
  final Decoration indicator;
  final TabBarIndicatorSize indicatorSize;
  final Color labelColor;
  final Color unSelectedLabelColor;
  final TextStyle labelStyle;
  final EdgeInsetsGeometry labelPadding;
  final TextStyle unSelectedLabelStyle;
  final DragStartBehavior dragStartBehavior;
  final OnTabClickListener onTabClickListener;

  _TabLayoutState({
    @required this.items,
    this.controller,
    this.isScrollable = false,
    this.indicatorColor,
    this.indicatorWeight = 2.0,
    this.indicatorPadding = EdgeInsets.zero,
    this.indicator,
    this.indicatorSize,
    this.labelColor,
    this.labelStyle,
    this.labelPadding,
    this.unSelectedLabelColor,
    this.unSelectedLabelStyle,
    this.dragStartBehavior = DragStartBehavior.start,
    @required this.onTabClickListener,
  });

  @override
  void initState() {
    controller = TabController(length: items.length, vsync: this);
    controller.addListener(() {
      print("hsd" + controller.index.toString());
      onTabClickListener.onTap(controller.index);
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: <Widget>[
        Container(
          color: Colors.red,
          child: TabBar(
            controller: controller,
            indicatorColor: indicatorColor ?? Colors.green,
            labelColor: labelColor ?? Colors.yellow,
            indicatorSize: indicatorSize ?? TabBarIndicatorSize.label,
            unselectedLabelColor: unSelectedLabelStyle ?? Colors.blueAccent,
            unselectedLabelStyle: unSelectedLabelStyle ??
                TextStyle(fontSize: 16, fontWeight: FontWeight.normal),
            indicatorPadding: indicatorPadding ?? EdgeInsets.zero,
            dragStartBehavior: dragStartBehavior ?? DragStartBehavior.start,
            indicatorWeight: indicatorWeight ?? 2,
            isScrollable: isScrollable ?? false,
            labelStyle: labelStyle ??
                TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
            tabs: new List.generate(
                items.length, (index) => Tab(text: items[index].title)),
          ),
        ),
        Flexible(
          child: TabBarView(
              controller: controller,
              children: new List.generate(
                  items.length, (index) => items[index].child)),
        )
      ],
    );
  }
}

abstract class OnTabClickListener {
  onTap(int index);
}
