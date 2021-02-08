import 'package:flutter/material.dart';
import 'package:password_manager/bloc/bloc.dart';
import 'package:password_manager/ui/screens/home.dart';
import 'package:password_manager/values/constants.dart';
import 'package:password_manager/values/dimens.dart';
import 'package:password_manager/values/strings.dart';
import 'package:shared_preferences/shared_preferences.dart';

class LoginPage extends StatefulWidget {
  @override
  _LoginPageState createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final _bloc = Bloc();
  final _formKey = GlobalKey<FormState>();
  final _controller = TextEditingController();
  SharedPreferences _sharedPreferences;

  @override
  void initState() {
    super.initState();
    _getInstances();
  }

  Future<void> _getInstances() async {
    _sharedPreferences = await SharedPreferences.getInstance();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(Strings.app_name),
      ),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(Dimens.normal_spacing),
          child: Container(
            height: MediaQuery.of(context).size.height * 0.5,
            child: Card(
              elevation: Dimens.small_spacing,
              child: Form(
                key: _formKey,
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    const Text(
                      Strings.master_code,
                      style: TextStyle(
                        fontSize: Dimens.large_text_size,
                      ),
                    ),
                    Padding(
                      padding: const EdgeInsets.all(Dimens.small_spacing),
                      child: TextFormField(
                        controller: _controller,
                        validator: _bloc.validator,
                        keyboardType: TextInputType.number,
                        decoration: const InputDecoration(
                          hintText: Strings.enter_four_digit_number,
                        ),
                      ),
                    ),
                    Builder(
                      builder: (context) {
                        return Row(
                          mainAxisAlignment: MainAxisAlignment.spaceAround,
                          children: [
                            RaisedButton(
                              onPressed: () => _signUp(context),
                              child: const Text(
                                Strings.sign_up,
                              ),
                            ),
                            RaisedButton(
                              onPressed: () => _login(context),
                              child: const Text(
                                Strings.login,
                              ),
                            ),
                          ],
                        );
                      },
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }

  void _login(BuildContext context) {
    if (_formKey.currentState.validate()) {
      var masterCode =
          _sharedPreferences.getInt(Constants.master_code_key) ?? null;
      if (masterCode == null) {
        _displaySnackBar(context, Strings.sign_up_required);
      } else {
        if (int.parse(_controller.text.trim()) == masterCode) {
          Navigator.of(context).pushReplacement(
            MaterialPageRoute(
              builder: (context) {
                return HomePage();
              },
            ),
          );
        } else {
          _displaySnackBar(context, Strings.enter_correct_code);
        }
      }
    }
  }

  Future<void> _signUp(BuildContext context) async {
    if (_formKey.currentState.validate()) {
      var masterCode =
          _sharedPreferences.getInt(Constants.master_code_key) ?? null;
      if (masterCode == null) {
        masterCode = int.parse(_controller.text.trim());
        await _sharedPreferences.setInt(Constants.master_code_key, masterCode);
        _displaySnackBar(context, Strings.sign_up_successful);
      } else {
        _displaySnackBar(context, Strings.already_signed_up);
      }
    }
  }

  void _displaySnackBar(BuildContext context, String content) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        duration: Duration(seconds: 1),
        content: Text(content),
      ),
    );
  }

  @override
  void dispose() {
    _controller.dispose();
    _bloc.dispose();
    super.dispose();
  }
}
