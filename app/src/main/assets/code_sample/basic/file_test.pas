{
1. Create new file with name: "input.in" and put it in the directory of application
    sdcard/PascalCompiler/
2. Write to file "input.in" some content:
    this is string
    1
    3.1415
    char
    4
    1 2 3 4
3. Save it
4. Run program
}
program file_reader;
var
    f1, f2: text;
    s: string;
    i, tmp, n: integer;
    d: real;
    char: char;

begin
    Assign(f1, 'input.in');
    Reset(f1);

    Assign(f2, 'output.inType');
    Rewrite(f2);

    {read string in file}
    readln(f1, s);
    writeln('read string: ', s);
    writeln(f2, s);

    {read integer value and store it to variable i}
    ReadLn(f1, i);
    writeln('read integer: ', i);
    writeln(f2, i);

    {read real value and store it to variable d}
    ReadLn(f1, d);
    writeln('read real: ', d);
    writeln(f2, d);

    {read char value and store it to variable char}
    readln(f1, char);
    writeln('read char: ', char);
    writeln(f2, char);

    readln(f1, n);
    for i := 1 to n do
    begin
        read(f1, tmp);
        writeln(tmp);
        write(f2, tmp, ' ');
    end;

    close(f1);
    Close(f2);
end.
{if you want to improve this code, please send code to me
tranleduy1233@gmail.com}