import 'package:flutter_test/flutter_test.dart';
import 'package:password_manager/bloc/bloc.dart';
import 'package:password_manager/values/strings.dart';


void main() {
  test('login page input validation', () {
    final _bloc = Bloc();
    final inputList = ['a#@d', '', '1111', '34534564'];
    for (var input in inputList) {
      if (input == inputList[0]) {
        expect(_bloc.validator(input), Strings.should_number);
      } else if (input == inputList[1]) {
        expect(_bloc.validator(input), Strings.code_required);
      } else if (input == inputList[2]) {
        expect(_bloc.validator(input), null);
      } else if (input == inputList[3]) {
        expect(_bloc.validator(input), Strings.should_four_digit);
      }
    }
    _bloc.dispose();
  });
}
