graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
  ]
  node [
    id 1
    label "1"
    type 2
    prc 3
  ]
  node [
    id 2
    label "2"
    type 2
    prc 3
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 3
    prb 5
    x 52
    y 87
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 6
    prb 2
    x 80
    y 19
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 8
    prb 2
    x 72
    y 36
  ]
  edge [
    source 0
    target 4
    bandwith 938
    delay 230
  ]
  edge [
    source 1
    target 5
    bandwith 315
    delay 430
  ]
  edge [
    source 2
    target 3
    bandwith 424
    delay 228
  ]
]
