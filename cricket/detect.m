function correlation = detect(signal, Freq, fs)

% constants
floatToByteShift = 128;

% y = e^(ju) = cos(u) + j*sin(u)
u = 2*pi*Freq/fs;

% realSum = real(signal * u) .* signal;
% imaginarySum = imag(signal * u) .* signal;
% 
% realAvg = mean(realSum);
% imaginaryAvg = mean(imaginarySum);

realSum = 0;
imaginarySum = 0;
for i = 1:length(signal)
    realSum = realSum + (cos(i*u) * signal(i));
    imaginarySum = imaginarySum + (sin(i*u) * signal(i));
end

realAvg = realSum/length(signal);
imaginaryAvg = imaginarySum/length(signal);

correlation = sqrt(power(realAvg, 2) + power(imaginaryAvg, 2));