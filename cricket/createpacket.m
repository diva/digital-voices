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

function signal = createpacket(info, fs, Freq, framelength)

shortsize = 15;
longsize = 24;
bigamplitude = 1;
smallamplitude = 0.5;

packet = [];
for k = 2:4
    switch info(k)
    case 1,
        packetlength = shortsize;
        silencelength = longsize;
        amplitude = bigamplitude;
    case 2,
        packetlength = shortsize;
        silencelength = longsize;
        amplitude = smallamplitude;
    case 3,
        packetlength = longsize;
        silencelength = shortsize;
        amplitude = bigamplitude;
    case 4,
        packetlength = longsize;
        silencelength = shortsize;
        amplitude = smallamplitude;
    otherwise
        displ('Wrong number, only valid 1,2,3,4');break;
    end
    framesamples = round(fs*framelength/1000);
    packetsamples = round(fs*packetlength/1000);
    silencesamples = round(fs*silencelength/1000);
    
    window = hanning(packetsamples);
    time = (1:packetsamples)*packetlength/(packetsamples*1000);
    smallpacket = amplitude*sin(2*pi*time(1:length(window))*Freq);
    smallpacket = smallpacket.*window';
    packet = [packet, zeros(1,round(silencesamples)), smallpacket];
end

signal = [zeros(1,round(fs*(info(1)-1)*(2*shortsize + 1.5*longsize)/1000)), packet];
signal = [signal, zeros(1,framesamples - length(signal))];

