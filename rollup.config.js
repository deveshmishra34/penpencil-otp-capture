import nodeResolve from '@rollup/plugin-node-resolve';

export default {
  input: 'dist/esm/index.js',
  output: {
    file: 'dist/plugin.js',
    format: 'iife',
    name: 'capacitorPlugin', // TODO: change this
    globals: {
      '@capacitor/core': 'capacitorExports',
    },
    sourcemap: true,
  },
  plugins: [
    nodeResolve({
      // allowlist of dependencies to bundle in
      // @see https://github.com/rollup/plugins/tree/master/packages/node-resolve#resolveonly
      resolveOnly: ['lodash'],
    }),
  ],
  onwarn: function(warning) {
    // Skip certain warnings

    // should intercept ... but doesn't in some rollup versions
    if ( warning.code === 'THIS_IS_UNDEFINED' ) { return; }

    // console.warn everything else
    console.warn( warning.message );
  }
};
