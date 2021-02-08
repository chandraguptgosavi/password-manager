import 'package:flutter/material.dart';
import 'package:password_manager/bloc/bloc.dart';
import 'package:password_manager/ui/screens/login_page.dart';
import 'package:password_manager/values/strings.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(
    Provider(
      create: (context) => Bloc(),
      child: PasswordManager(),
    ),
  );
}

class PasswordManager extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: Strings.app_name,
      theme: ThemeData(
        elevatedButtonTheme: ElevatedButtonThemeData(
          style: ButtonStyle(
            backgroundColor: MaterialStateProperty.all(Colors.blue),
            textStyle: MaterialStateProperty.all(
              TextStyle(
                color: Colors.white,
              ),
            ),
          ),
        ),
      ),
      home: LoginPage(),
    );
  }
}
