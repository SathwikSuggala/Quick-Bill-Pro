import os
from svglib.svglib import svg2rlg
from reportlab.graphics import renderPM

def convert_svg_to_png():
    # Create icons directory if it doesn't exist
    if not os.path.exists('src/assets/icons'):
        os.makedirs('src/assets/icons')
    
    # List of SVG files to convert
    svg_files = [
        'dashboard.svg',
        'billing.svg',
        'inlets.svg',
        'outlets.svg',
        'products.svg',
        'reports.svg',
        'settings.svg',
        'user.svg'
    ]
    
    # Convert each SVG file to PNG
    for svg_file in svg_files:
        svg_path = f'src/assets/icons/{svg_file}'
        png_path = f'src/assets/icons/{os.path.splitext(svg_file)[0]}.png'
        
        if os.path.exists(svg_path):
            try:
                drawing = svg2rlg(svg_path)
                renderPM.drawToFile(drawing, png_path, fmt="PNG", dpi=72)
                print(f'Converted {svg_file} to PNG')
            except Exception as e:
                print(f'Error converting {svg_file}: {str(e)}')
        else:
            print(f'File not found: {svg_path}')

if __name__ == '__main__':
    convert_svg_to_png() 