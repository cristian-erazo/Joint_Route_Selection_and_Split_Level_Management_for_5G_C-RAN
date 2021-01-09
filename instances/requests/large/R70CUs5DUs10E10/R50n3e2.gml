graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
  ]
  node [
    id 1
    label "1"
    type 1
    prc 4
    ant 2
    prb 5
    x 97
    y 56
  ]
  node [
    id 2
    label "2"
    type 1
    prc 4
    ant 2
    prb 4
    x 85
    y 16
  ]
  edge [
    source 0
    target 2
    bandwith 680
    delay 320
  ]
  edge [
    source 0
    target 1
    bandwith 463
    delay 183
  ]
]
