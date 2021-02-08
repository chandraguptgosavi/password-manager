import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:password_manager/bloc/bloc.dart';
import 'package:password_manager/values/dimens.dart';
import 'package:password_manager/values/strings.dart';
import 'package:provider/provider.dart';

class EnterDetailsPage extends StatefulWidget {
  @override
  _EnterDetailsPageState createState() => _EnterDetailsPageState();
}

class _EnterDetailsPageState extends State<EnterDetailsPage> {
  Bloc _bloc;
  bool isAccountNull;
  final _formKey = GlobalKey<FormState>();
  final _titleController = TextEditingController();
  final _usernameController = TextEditingController();
  final _passwordController = TextEditingController();
  final _packageController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    _bloc = Provider.of<Bloc>(context, listen: false);

    return Scaffold(
      appBar: AppBar(
        title: const Text(Strings.enter_details),
      ),
      body: SingleChildScrollView(
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              Container(
                height: MediaQuery.of(context).size.height * 0.16,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  children: [
                    Flexible(
                      flex: 1,
                      child: const Text(
                        Strings.title,
                        style: TextStyle(
                          fontSize: Dimens.large_text_size,
                        ),
                      ),
                    ),
                    Flexible(
                      flex: 2,
                      child: TextFormField(
                        validator: _validate,
                        controller: _titleController,
                        decoration: const InputDecoration(
                          hintText: Strings.title_hint,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              Container(
                height: MediaQuery.of(context).size.height * 0.16,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  children: [
                    Flexible(
                      flex: 1,
                      child: const Text(
                        Strings.username,
                        style: TextStyle(
                          fontSize: Dimens.large_text_size,
                        ),
                      ),
                    ),
                    Flexible(
                      flex: 2,
                      child: TextFormField(
                        controller: _usernameController,
                        validator: _validate,
                        decoration: const InputDecoration(
                          hintText: Strings.username_hint,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              Container(
                height: MediaQuery.of(context).size.height * 0.16,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  children: [
                    Flexible(
                      flex: 1,
                      child: const Text(
                        Strings.password,
                        style: TextStyle(
                          fontSize: Dimens.large_text_size,
                        ),
                      ),
                    ),
                    Flexible(
                      flex: 2,
                      child: TextFormField(
                        controller: _passwordController,
                        validator: _validate,
                        decoration: const InputDecoration(
                          hintText: Strings.password_hint,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              Container(
                height: MediaQuery.of(context).size.height * 0.16,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  children: [
                    Flexible(
                      flex: 1,
                      child: const Text(
                        Strings.package,
                        style: TextStyle(
                          fontSize: Dimens.large_text_size,
                        ),
                      ),
                    ),
                    Flexible(
                      flex: 2,
                      child: TextFormField(
                        controller: _packageController,
                        decoration: const InputDecoration(
                          hintText: Strings.package_hint,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              Container(
                height: MediaQuery.of(context).size.height * 0.16,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    RaisedButton(
                      onPressed: () async {
                        await _onSavePressed(context);
                      },
                      child: const Text(
                        Strings.save,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _onSavePressed(BuildContext context) async {
    if (_formKey.currentState.validate()) {
      await _bloc.save(
        _titleController.text.trim(),
        _usernameController.text.trim(),
        _passwordController.text.trim(),
        _packageController.text.trim(),
      );
      Navigator.of(context).pop();
    }
  }

  String _validate(value) {
    if (value.isEmpty) {
      return Strings.required_field;
    }
    return null;
  }

  @override
  void dispose() {
    super.dispose();
    _titleController.dispose();
    _usernameController.dispose();
    _passwordController.dispose();
    _packageController.dispose();
  }
}
