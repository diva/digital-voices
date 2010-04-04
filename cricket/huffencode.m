% Copyright 2001 by the authors. All rights reserved.
% Authors: Cristina V Lopes (crista at tagide dot com)
%          Patricio de la Guardia
%
% Permission is hereby granted, free of charge, to any person obtaining a copy
% of this software and associated documentation files (the "Software"), to deal
% in the Software without restriction, including without limitation the rights
% to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
% copies of the Software, and to permit persons to whom the Software is
% furnished to do so, subject to the following conditions:
%
% The above copyright notice and this permission notice shall be included in
% all copies or substantial portions of the Software.
%
% THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
% IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
% FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
% AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
% LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
% OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
% THE SOFTWARE.

function output = huffencode(input)
output = [];
for i = 1:length(input)
    switch input(i)
    case {' ',','}
        output = [output, 4];
    case {'a','A'}
        output = [output,1, 4];
    case {'b','B'}
        output = [output,2, 4,4,2 ];
    case {'c','C'}
        output = [output,1,1,3 ];
    case {'d','D'}
        output = [output,1,1,2 ];
    case {'e','E'}
        output = [output,1,2 ];
    case {'f','F'}
        output = [output, 2,4,3];
    case {'g','G'}
        output = [output, 2,3,2];
    case {'h','H'}
        output = [output, 3,4];
    case {'i','I'}
        output = [output,2,2 ];
    case {'j','J'}
        output = [output, 2,4,2,2];
    case {'k','K'}
        output = [output, 2,4,4,4];
    case {'l','L'}
        output = [output, 1,1,1];
    case {'m','M'}
        output = [output, 2,4,1];
    case {'n','N'}
        output = [output, 3,1];
    case {'o','O'}
        output = [output, 2,1];
    case {'p','P'}
        output = [output, 2,3,1];
    case {'q','Q'}
        output = [output,2,4,2,3 ];
    case {'r','R'}
        output = [output, 3,3];
    case {'s','S'}
        output = [output, 3,2];
    case {'t','T'}
        output = [output, 1,3];
    case {'u','U'}
        output = [output, 1,1,4];
    case {'v','V'}
        output = [output, 2,4,4,3];
    case {'w','W'}
        output = [output, 2,3,3];
    case {'x','X'}
        output = [output, 2,4,2,1];
    case {'y','Y'}
        output = [output, 2,4,4,1];
    case {'z','Z'}
        output = [output, 2,4,2,4];
    case {'.',';'}
        output = [output, 2,3,4];
        
    otherwise
        disp('ERROR: Character not known!!');
        disp(input(i))
    end
end
