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

function output = huffdecode(input)
output = [];
%for i = 1 : length(input)
i = 1;
while i <= length(input) - 3,
    switch input(i)
    case 4
        output = [output, ' '];
        %disp(' ');
        i = i + 1;
        
    case 3
        switch input(i+1)
        case 1
            output = [output, 'N'];
            %disp('N');
        case 2
            output = [output, 'S'];
            %disp('S');
        case 3
            output = [output, 'R'];
            %disp('R');
        case 4
            output = [output, 'H'];
            %disp('H');
        otherwise
            disp('error inside case 3')
            break
        end
        i = i + 2;
        
    case 2        
        
        switch input(i+1)
        case 1
            output = [output, 'O'];
            %disp('O');
            i = i + 2;
        case 2
            output = [output, 'I'];
            %disp('I');
            i = i + 2;
        case 3
            
            switch input(i+2)
            case 1
                output = [output, 'P'];
                %disp('P');
            case 2
                output = [output, 'G'];
                %disp('G');
            case 3
                output = [output, 'W'];
                %disp('W');
            case 4
                output = [output, '.'];
                %disp('.');
            otherwise
                disp('error inside case 23')
                break
            end
            i = i + 3;
        case 4
            switch input(i+2)
            case 1
                output = [output, 'M'];
                %disp('M');
                i = i + 3;
                
            case 2
                switch input(i+3)
                case 1
                    output = [output, 'X'];
                    %disp('X');
                case 2
                    output = [output, 'J'];
                    %disp('J');
                case 3
                    output = [output, 'Q'];
                    %disp('Q');
                case 4
                    output = [output, 'Z'];
                    %disp('Z');
                otherwise
                    disp('error inside case 242')
                    break
                end
                i = i + 4;
            case 3
                output = [output, 'F'];
                %disp('F');
                i = i + 3;
                
            case 4
                switch input(i+3)
                case 1
                    output = [output, 'Y'];
                    %disp('Y');
                case 2
                    output = [output, 'B'];
                    %disp('B');
                case 3
                    output = [output, 'V'];
                    %disp('V');
                case 4
                    output = [output, 'K'];
                    %disp('K');
                otherwise
                    disp('error inside case 244')
                    break
                end
                i = i + 4;
            otherwise
                disp('error inside case 24')
                break
            end
        otherwise
            disp('error inside case 2')
            break
        end
        
        
    case 1
        switch input(i+1)
        case 1
            switch input(i+2)
            case 1
                output = [output, 'L'];
                %disp('L');
            case 2
                output = [output, 'D'];
                %disp('D');

            case 3
                output = [output, 'C'];
                %disp('C');
            case 4
                output = [output, 'U'];
                %disp('U');
            otherwise
                disp('error inside case 11')
                break
            end
            i = i + 3;
        
        case 2
            output = [output, 'E'];
            %disp('E');
            i = i + 2;
            
        case 3
            output = [output, 'T'];
            %disp('T');
            i = i + 2;
            
        case 4
            output = [output, 'A'];;
            %disp('A');
            i = i + 2;
            
        otherwise
            disp('error inside case 1')
            break
        end
        
        
    otherwise
        
    end
end
