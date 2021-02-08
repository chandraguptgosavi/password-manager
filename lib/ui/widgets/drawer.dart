import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:password_manager/bloc/bloc.dart';
import 'package:password_manager/values/strings.dart';

class NavigationDrawer extends StatefulWidget {
  @override
  _NavigationDrawerState createState() => _NavigationDrawerState();
}

class _NavigationDrawerState extends State<NavigationDrawer> {
  final _bloc = Bloc();

  @override
  Widget build(BuildContext context) {
    return AnnotatedRegion<SystemUiOverlayStyle>(
      value: SystemUiOverlayStyle(
        statusBarColor: Theme.of(context).primaryColorDark,
      ),
      child: Drawer(
        child: ListView(
          children: [
            DrawerHeader(
              child: const FlutterLogo(),
            ),
            ListTile(
              onTap: () async {
                Navigator.of(context).pop();
                await _showAutofillDialog(context);
              },
              title: const Text(Strings.enable_autofill),
            ),
            ListTile(
              onTap: () async {
                Navigator.of(context).pop();
                await _bloc.enableAutofillSuccessfulWindow();
              },
              title: const Text(Strings.enable_autofill_successful_window),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _showAutofillDialog(BuildContext context) async {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text(Strings.autofill_dialog_title),
          content: SingleChildScrollView(
              child: const Text(Strings.autofill_dialog_content)),
          actions: <Widget>[
            TextButton(
              child: const Text(Strings.ok),
              onPressed: () async {
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }

  @override
  void dispose() {
    _bloc.dispose();
    super.dispose();
  }
}
