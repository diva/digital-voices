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


clear all
close all

fs= 22050;
Freq = 4184;
framelength = 420; %miliseconds
% packetlength = 17; %miliseconds
% silencelength = 22; %miliseconds
FILE = 'data.txt';
[datain, count] = fread(fopen(FILE, 'r'), 'uint8');
% texto = [];
% for k = 1: count
%     texto = [texto,char(datain(k))];
% end
info = huffencode(datain);
signal = [];
encoded = [];
for i = 1:4:length(info) - 4
    packet = createpacket(info(i:i+4), fs, Freq, framelength);
    signal = [signal, packet];
end

% include a hail and EOT packet
% signal = [createpacket([1 3 3 3], fs, Freq, framelength) signal];
window = hanning(0.1*fs);
time = 0:1/fs:(fs*0.1-1)/fs;
hail = sin(2*pi*time*Freq);
hail = hail.*window';
signal = [hail signal hail];

%signal = signal + .08*rand(1,length(signal));
plot(signal);
wavwrite(signal/max(abs(signal)),fs,'signal.wav');
